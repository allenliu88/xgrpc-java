package io.xgrpc.client.transport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.xgrpc.api.ability.ClientAbilities;
import io.xgrpc.api.common.Constants;
import io.xgrpc.api.exception.XgrpcException;
import io.xgrpc.api.exception.runtime.XgrpcRuntimeException;
import io.xgrpc.api.remote.RemoteConstants;
import io.xgrpc.api.remote.request.Request;
import io.xgrpc.api.remote.response.Response;
import io.xgrpc.client.utils.AppNameUtils;
import io.xgrpc.client.utils.EnvUtil;
import io.xgrpc.client.utils.ParamUtil;
import io.xgrpc.common.notify.Event;
import io.xgrpc.common.notify.NotifyCenter;
import io.xgrpc.common.notify.listener.Subscriber;
import io.xgrpc.common.remote.ConnectionType;
import io.xgrpc.common.remote.client.ConnectionEventListener;
import io.xgrpc.common.remote.client.RpcClient;
import io.xgrpc.common.remote.client.RpcClientFactory;
import io.xgrpc.common.remote.client.ServerListFactory;
import io.xgrpc.common.remote.client.ServerRequestHandler;
import io.xgrpc.common.utils.MD5Utils;
import io.xgrpc.common.utils.StringUtils;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRpcClientManager implements RpcClientManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRpcClientManager.class);
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String DEFAULT_TENANT = "0";
    private static final String DEFAULT_TASK_ID = "0";
    private String tenant;
    private String uuid = UUID.randomUUID().toString();
    private ConnectionType connectionType;
    private ServerListManager serverListManager;
    /**
     * listener called where connection's status changed.
     */
    protected List<ConnectionEventListener> connectionEventListeners = new ArrayList<>();

    /**
     * handlers to process server push request.
     */
    protected List<ServerRequestHandler> serverRequestHandlers = new ArrayList<>();

    protected Map<String, String> labels = new HashMap<>();

    public DefaultRpcClientManager() {
    }

    public DefaultRpcClientManager(ConnectionType connectionType, ServerListManager serverListManager) {
        this.connectionType = connectionType;
        this.serverListManager = serverListManager;
    }

    public String getBuilderName() {
        return this.uuid;
    }

    public DefaultRpcClientManager setTenant(String tenant) {
        this.tenant = tenant;
        return this;
    }

    public DefaultRpcClientManager setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
        return this;
    }

    public DefaultRpcClientManager setServerListManager(ServerListManager serverListManager) {
        this.serverListManager = serverListManager;
        return this;
    }

    public DefaultRpcClientManager addConnectionEventListener(ConnectionEventListener connectionEventListener) {
        this.connectionEventListeners.add(connectionEventListener);
        return this;
    }

    public DefaultRpcClientManager addServerRequestHandler(ServerRequestHandler serverRequestHandler) {
        this.serverRequestHandlers.add(serverRequestHandler);
        return this;
    }

    public DefaultRpcClientManager addLabel(String key, String value) {
        this.labels.put(key, value);
        return this;
    }

    /**
     * 构造请求客户端
     *
     * @param taskId 任务ID
     * @return 客户端
     */
    @Override
    public RpcClient build(String taskId) {
        Asserts.notNull(this.connectionType, "connection type cannot be null.");
        Asserts.notNull(this.serverListManager, "server list factory cannot be null.");

        taskId = StringUtils.isBlank(taskId) ? DEFAULT_TASK_ID : taskId;
        this.labels.putIfAbsent("taskId", taskId);
        this.initRpcClientDefaultLabels();
        RpcClient rpcClient = RpcClientFactory.createClient(this.uuid + "-" + taskId, this.connectionType, this.labels);

        if (!rpcClient.isWaitInitiated()) {
            // 如果不是等待初始化状态，则直接返回已有Client复用
            return rpcClient;
        }

        rpcClient.setTenant(StringUtils.isBlank(this.tenant) ? DEFAULT_TENANT : this.tenant);

        // 初始化默认值
        this.initRpcClientDefaultHandler(rpcClient);
        this.initRpcClientDefaultAbilities(rpcClient);

        // 组装用户自定义处理器
        this.connectionEventListeners.forEach(item -> rpcClient.registerConnectionListener(item));
        this.serverRequestHandlers.forEach(item -> rpcClient.registerServerRequestHandler(item));

        try {
            rpcClient.start();
        } catch (Exception ex) {
            throw new XgrpcRuntimeException(ex);
        }

        return rpcClient;
    }

    /**
     * 发起请求
     *
     * @param rpcClientInner 客户端
     * @param request 请求
     * @param timeoutMills 超时时间
     * @return 响应
     */
    @Override
    public Response request(RpcClient rpcClientInner, Request request, long timeoutMills) {
        try {
            request.putAllHeader(this.getCommonHeader());
        } catch (Exception ex) {
            throw new XgrpcRuntimeException(XgrpcException.CLIENT_INVALID_PARAM, ex);
        }
        JsonObject asJsonObjectTemp = new Gson().toJsonTree(request).getAsJsonObject();
        asJsonObjectTemp.remove("headers");
        asJsonObjectTemp.remove("requestId");
        boolean limit = Limiter.isLimit(request.getClass() + asJsonObjectTemp.toString());
        if (limit) {
            throw new XgrpcRuntimeException(XgrpcException.CLIENT_OVER_THRESHOLD,
                    "More than client-side current limit threshold");
        }

        try {
            return rpcClientInner.request(request, timeoutMills);
        } catch (Exception ex) {
            throw new XgrpcRuntimeException(ex);
        }
    }

    /**
     * 关闭所有由该Builder产生的客户端
     */
    @Override
    public void shutdown() {
        synchronized (RpcClientFactory.getAllClientEntries()) {
            LOGGER.info("Trying to shutdown transport client {}", this);
            Set<Map.Entry<String, RpcClient>> allClientEntries = RpcClientFactory.getAllClientEntries();
            Iterator<Map.Entry<String, RpcClient>> iterator = allClientEntries.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, RpcClient> entry = iterator.next();
                if (entry.getKey().startsWith(uuid)) {
                    LOGGER.info("Trying to shutdown rpc client {}", entry.getKey());

                    try {
                        entry.getValue().shutdown();
                    } catch (XgrpcException XgrpcException) {
                        XgrpcException.printStackTrace();
                    }
                    LOGGER.info("Remove rpc client {}", entry.getKey());
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 初始化Labels
     */
    private void initRpcClientDefaultLabels() {
        labels.put(RemoteConstants.LABEL_SOURCE, RemoteConstants.LABEL_SOURCE_SDK);
        labels.put(RemoteConstants.LABEL_MODULE, RemoteConstants.LABEL_MODULE_CONFIG);
        labels.put(Constants.APPNAME, AppNameUtils.getAppName());
        labels.put(Constants.VIPSERVER_TAG, EnvUtil.getSelfVipserverTag());
        labels.put(Constants.AMORY_TAG, EnvUtil.getSelfAmoryTag());
        labels.put(Constants.LOCATION_TAG, EnvUtil.getSelfLocationTag());
    }

    /**
     * 初始化默认Handler
     *
     * @param rpcClientInner rpc client
     */
    private void initRpcClientDefaultHandler(final RpcClient rpcClientInner) {
        /*
         * Register Change / ReSync Handler
         */
        rpcClientInner.registerServerRequestHandler((request) -> {
            // Server Request Handler
            return null;
        });

        rpcClientInner.registerConnectionListener(new ConnectionEventListener() {

            @Override
            public void onConnected() {
                LOGGER.info("[{}] Connected,notify listen context...", rpcClientInner.getName());
            }

            @Override
            public void onDisConnect() {
                String taskId = rpcClientInner.getLabels().get("taskId");
                LOGGER.info("[{}] DisConnected,clear listen context...", rpcClientInner.getName());
            }

        });

        rpcClientInner.serverListFactory(new ServerListFactory() {
            @Override
            public String genNextServer() {
                return serverListManager.getNextServerAddr();

            }

            @Override
            public String getCurrentServer() {
                return serverListManager.getCurrentServerAddr();

            }

            @Override
            public List<String> getServerList() {
                return serverListManager.getServerUrls();

            }
        });

        NotifyCenter.registerSubscriber(new Subscriber<ServerlistChangeEvent>() {
            @Override
            public void onEvent(ServerlistChangeEvent event) {
                rpcClientInner.onServerListChange();
            }

            @Override
            public Class<? extends Event> subscribeType() {
                return ServerlistChangeEvent.class;
            }
        });
    }

    /**
     * 初始化默认的Abilities
     * @param rpcClientInner rpc client
     */
    private void initRpcClientDefaultAbilities(final RpcClient rpcClientInner) {
        ClientAbilities clientAbilities = new ClientAbilities();
        clientAbilities.getRemoteAbility().setSupportRemoteConnection(true);
        rpcClientInner.clientAbilities(clientAbilities);
    }

    /**
     * get common header.
     *
     * @return headers.
     */
    private Map<String, String> getCommonHeader() {
        Map<String, String> headers = new HashMap<>(16);

        String ts = String.valueOf(System.currentTimeMillis());
        String token = MD5Utils.md5Hex(ts + ParamUtil.getAppKey(), Constants.ENCODE);

        headers.put(Constants.CLIENT_APPNAME_HEADER, ParamUtil.getAppName());
        headers.put(Constants.CLIENT_REQUEST_TS_HEADER, ts);
        headers.put(Constants.CLIENT_REQUEST_TOKEN_HEADER, token);
        headers.put(Constants.CHARSET_KEY, DEFAULT_CHARSET);
        return headers;
    }
}
