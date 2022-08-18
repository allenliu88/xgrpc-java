/*
 *  Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.xgrpc.sys.env;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.xgrpc.common.JustForTest;
import io.xgrpc.common.utils.ConvertUtils;
import io.xgrpc.common.utils.IoUtils;
import io.xgrpc.common.utils.StringUtils;
import io.xgrpc.common.utils.ThreadUtils;
import io.xgrpc.sys.utils.DiskUtils;
import io.xgrpc.sys.utils.InetUtils;

/**
 * Its own configuration information manipulation tool class.
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class EnvUtil {
    
    public static final String STANDALONE_MODE_ALONE = "standalone";
    
    public static final String STANDALONE_MODE_CLUSTER = "cluster";
    
    public static final String FUNCTION_MODE_CONFIG = "config";
    
    public static final String FUNCTION_MODE_NAMING = "naming";
    
    /**
     * The key of xgrpc home.
     */
    public static final String XGRPC_HOME_KEY = "xgrpc.home";
    
    private static volatile String localAddress = "";
    
    private static int port = -1;
    
    private static Boolean isStandalone = null;
    
    private static String functionModeType = null;
    
    private static String contextPath = null;
    
    private static final String FILE_PREFIX = "file:";
    
    private static final String SERVER_PORT_PROPERTY = "server.port";
    
    private static final int DEFAULT_SERVER_PORT = 8848;
    
    private static final String DEFAULT_WEB_CONTEXT_PATH = "/xgrpc";
    
    private static final String MEMBER_LIST_PROPERTY = "xgrpc.member.list";
    
    private static final String XGRPC_HOME_PROPERTY = "user.home";
    
    private static final String DEFAULT_ADDITIONAL_PATH = "conf";
    
    private static final String DEFAULT_ADDITIONAL_FILE = "cluster.conf";
    
    private static final String XGRPC_HOME_ADDITIONAL_FILEPATH = "xgrpc";
    
    private static final String XGRPC_TEMP_DIR_1 = "data";
    
    private static final String XGRPC_TEMP_DIR_2 = "tmp";
    
    @JustForTest
    private static String confPath = "";
    
    @JustForTest
    private static String xgrpcHomePath = null;
    
    public static String getProperty(String key) {
        String value = System.getProperty(key);
        if (StringUtils.isBlank(value)) {
            value = System.getenv(key);
        }

        return value;
    }
    
    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);

        if (StringUtils.isBlank(value)) {
            value = defaultValue;
        }

        return value;
    }
    
    public static <T> T getProperty(String key, Class<T> targetType) {
        String value = getProperty(key);
        if (StringUtils.isBlank(value)) {
            return null;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(value, targetType);
        } catch (Exception ex) {
            //
            return null;
        }
    }
    
    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        T ret = getProperty(key, targetType);

        return ret != null ? ret : defaultValue;
    }

    public static List<String> getPropertyList(String key) {
        List<String> valueList = new ArrayList<>();
        
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            String value = System.getProperty(key + "[" + i + "]");
            if (StringUtils.isBlank(value)) {
                break;
            }
            
            valueList.add(value);
        }
        
        return valueList;
    }
    
    public static String getLocalAddress() {
        if (StringUtils.isBlank(localAddress)) {
            localAddress = InetUtils.getSelfIP() + ":" + getPort();
        }
        return localAddress;
    }
    
    public static void setLocalAddress(String localAddress) {
        EnvUtil.localAddress = localAddress;
    }
    
    public static int getPort() {
        if (port == -1) {
            port = getProperty(SERVER_PORT_PROPERTY, Integer.class, DEFAULT_SERVER_PORT);
        }
        return port;
    }
    
    public static void setPort(int port) {
        EnvUtil.port = port;
    }
    
    public static String getContextPath() {
        if (Objects.isNull(contextPath)) {
            contextPath = getProperty(Constants.WEB_CONTEXT_PATH, DEFAULT_WEB_CONTEXT_PATH);
            if (Constants.ROOT_WEB_CONTEXT_PATH.equals(contextPath)) {
                contextPath = StringUtils.EMPTY;
            }
        }
        return contextPath;
    }
    
    public static void setContextPath(String contextPath) {
        EnvUtil.contextPath = contextPath;
    }
    
    @JustForTest
    public static void setIsStandalone(Boolean isStandalone) {
        EnvUtil.isStandalone = isStandalone;
    }
    
    /**
     * Whether open upgrade from 1.X xgrpc server. Might effect `doubleWrite` and `Old raft`.
     *
     * @since 2.1.0
     * @return {@code true} open upgrade feature, otherwise {@code false}, default {@code false}
     * @deprecated 2.2.0
     */
    public static boolean isSupportUpgradeFrom1X() {
        return ConvertUtils.toBoolean(getProperty(Constants.SUPPORT_UPGRADE_FROM_1X), false);
    }
    
    /**
     * Standalone mode or not.
     */
    public static boolean getStandaloneMode() {
        if (Objects.isNull(isStandalone)) {
            isStandalone = Boolean.getBoolean(Constants.STANDALONE_MODE_PROPERTY_NAME);
        }
        return isStandalone;
    }
    
    /**
     * server function mode.
     */
    public static String getFunctionMode() {
        if (StringUtils.isEmpty(functionModeType)) {
            functionModeType = System.getProperty(Constants.FUNCTION_MODE_PROPERTY_NAME);
        }
        return functionModeType;
    }
    
    private static String xgrpcTmpDir;
    
    public static String getXgrpcTmpDir() {
        if (StringUtils.isBlank(xgrpcTmpDir)) {
            xgrpcTmpDir = Paths.get(getXgrpcHome(), XGRPC_TEMP_DIR_1, XGRPC_TEMP_DIR_2).toString();
        }
        return xgrpcTmpDir;
    }
    
    public static String getXgrpcHome() {
        if (StringUtils.isBlank(xgrpcHomePath)) {
            String xgrpcHome = System.getProperty(XGRPC_HOME_KEY);
            if (StringUtils.isBlank(xgrpcHome)) {
                xgrpcHome = Paths.get(System.getProperty(XGRPC_HOME_PROPERTY), XGRPC_HOME_ADDITIONAL_FILEPATH).toString();
            }
            return xgrpcHome;
        }
        // test-first
        return xgrpcHomePath;
    }
    
    @JustForTest
    public static void setXgrpcHomePath(String xgrpcHomePath) {
        EnvUtil.xgrpcHomePath = xgrpcHomePath;
    }
    
    public static List<String> getIPsBySystemEnv(String key) {
        String env = getSystemEnv(key);
        List<String> ips = new ArrayList<>();
        if (StringUtils.isNotEmpty(env)) {
            ips = Arrays.asList(env.split(","));
        }
        return ips;
    }
    
    public static String getSystemEnv(String key) {
        return System.getenv(key);
    }
    
    public static float getLoad() {
        return (float) OperatingSystemBeanManager.getOperatingSystemBean().getSystemLoadAverage();
    }

    public static float getCpu() {
        return (float) OperatingSystemBeanManager.getSystemCpuUsage();
    }
    
    public static float getMem() {
        return (float) (1 - OperatingSystemBeanManager.getFreePhysicalMem() / OperatingSystemBeanManager.getTotalPhysicalMem());
    }
    
    public static String getConfPath() {
        if (StringUtils.isNotBlank(EnvUtil.confPath)) {
            return EnvUtil.confPath;
        }
        EnvUtil.confPath = Paths.get(getXgrpcHome(), DEFAULT_ADDITIONAL_PATH).toString();
        return confPath;
    }
    
    public static void setConfPath(final String confPath) {
        EnvUtil.confPath = confPath;
    }
    
    public static String getClusterConfFilePath() {
        return Paths.get(getXgrpcHome(), DEFAULT_ADDITIONAL_PATH, DEFAULT_ADDITIONAL_FILE).toString();
    }
    
    /**
     * read cluster.conf to ip list.
     *
     * @return ip list.
     * @throws IOException ioexception {@link IOException}
     */
    public static List<String> readClusterConf() throws IOException {
        try (Reader reader = new InputStreamReader(new FileInputStream(new File(getClusterConfFilePath())),
                StandardCharsets.UTF_8)) {
            return analyzeClusterConf(reader);
        } catch (FileNotFoundException ignore) {
            List<String> tmp = new ArrayList<>();
            String clusters = EnvUtil.getMemberList();
            if (StringUtils.isNotBlank(clusters)) {
                String[] details = clusters.split(",");
                for (String item : details) {
                    tmp.add(item.trim());
                }
            }
            return tmp;
        }
    }
    
    /**
     * read file stream to ip list.
     *
     * @param reader reader
     * @return ip list.
     * @throws IOException IOException
     */
    public static List<String> analyzeClusterConf(Reader reader) throws IOException {
        List<String> instanceList = new ArrayList<String>();
        List<String> lines = IoUtils.readLines(reader);
        String comment = "#";
        for (String line : lines) {
            String instance = line.trim();
            if (instance.startsWith(comment)) {
                // # it is ip
                continue;
            }
            if (instance.contains(comment)) {
                // 192.168.71.52:8848 # Instance A
                instance = instance.substring(0, instance.indexOf(comment));
                instance = instance.trim();
            }
            int multiIndex = instance.indexOf(Constants.COMMA_DIVISION);
            if (multiIndex > 0) {
                // support the format: ip1:port,ip2:port  # multi inline
                instanceList.addAll(Arrays.asList(instance.split(Constants.COMMA_DIVISION)));
            } else {
                //support the format: 192.168.71.52:8848
                instanceList.add(instance);
            }
        }
        return instanceList;
    }
    
    public static void writeClusterConf(String content) throws IOException {
        DiskUtils.writeFile(new File(getClusterConfFilePath()), content.getBytes(StandardCharsets.UTF_8), false);
    }
    
    public static String getMemberList() {
        String val = System.getenv(MEMBER_LIST_PROPERTY);
        if (StringUtils.isBlank(val)) {
            val = System.getProperty(MEMBER_LIST_PROPERTY);
        }

        return val;
    }
    
    /**
     * Get available processor numbers from environment.
     *
     * <p>
     *     If there are setting of {@code xgrpc.core.sys.basic.processors} in config/JVM/system, use it.
     *     If no setting, use the one time {@code ThreadUtils.getSuitableThreadCount()}.
     * </p>
     *
     * @return available processor numbers from environment, will not lower than 1.
     */
    public static int getAvailableProcessors() {
        int result = getProperty(Constants.AVAILABLE_PROCESSORS_BASIC, int.class,
                ThreadUtils.getSuitableThreadCount(1));
        return result > 0 ? result : 1;
    }
    
    /**
     * Get a multiple time of available processor numbers from environment.
     *
     * @param multiple multiple of available processor numbers
     * @return available processor numbers from environment, will not lower than 1.
     */
    public static int getAvailableProcessors(int multiple) {
        if (multiple < 1) {
            throw new IllegalArgumentException("processors multiple must upper than 1");
        }
        Integer processor = getProperty(Constants.AVAILABLE_PROCESSORS_BASIC, Integer.class);
        return null != processor && processor > 0 ? processor * multiple : ThreadUtils.getSuitableThreadCount(multiple);
    }
    
    /**
     * Get a scale of available processor numbers from environment.
     *
     * @param scale scale from 0 to 1.
     * @return available processor numbers from environment, will not lower than 1.
     */
    public static int getAvailableProcessors(double scale) {
        if (scale < 0 || scale > 1) {
            throw new IllegalArgumentException("processors scale must between 0 and 1");
        }
        double result = getProperty(Constants.AVAILABLE_PROCESSORS_BASIC, int.class,
                ThreadUtils.getSuitableThreadCount(1)) * scale;
        return result > 1 ? (int) result : 1;
    }
}
