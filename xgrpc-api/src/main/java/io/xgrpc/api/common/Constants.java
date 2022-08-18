package io.xgrpc.api.common;

import java.util.concurrent.TimeUnit;

public class Constants {
    private Constants() {}

    /**
     * The constants in config directory.
     */
    public static class Config {

        public static final String CONFIG_MODULE = "config";

        public static final String NOTIFY_HEADER = "notify";
    }

    /**
     * The constants in naming directory.
     */
    public static class Naming {

        public static final String NAMING_MODULE = "naming";

        public static final String CMDB_CONTEXT_TYPE = "CMDB";
    }

    public static final String DEFAULT_GROUP = "DEFAULT_GROUP";

    public static final String DATAID = "dataId";

    public static final String GROUP = "group";

    public static final long DEFAULT_HEART_BEAT_TIMEOUT = TimeUnit.SECONDS.toMillis(15);

    public static final long DEFAULT_IP_DELETE_TIMEOUT = TimeUnit.SECONDS.toMillis(30);

    public static final long DEFAULT_HEART_BEAT_INTERVAL = TimeUnit.SECONDS.toMillis(5);

    public static final String NAMING_HTTP_HEADER_SPLITTER = "\\|";

    public static final String NULL = "";

    public static final Integer SDK_GRPC_PORT_DEFAULT_OFFSET = 1000;

    public static final Integer CLUSTER_GRPC_PORT_DEFAULT_OFFSET = 1001;

    public static final String SERVICE_INFO_SPLITER = "@@";

    public static final int SERVICE_INFO_SPLIT_COUNT = 2;

    public static final String NULL_STRING = "null";

    public static final String NUMBER_PATTERN_STRING = "^\\d+$";

    public static final String ANY_PATTERN = ".*";

    public static final String DEFAULT_INSTANCE_ID_GENERATOR = "simple";

    public static final String SNOWFLAKE_INSTANCE_ID_GENERATOR = "snowflake";

    public static final String HTTP_PREFIX = "http";

    public static final String ALL_PATTERN = "*";

    public static final String COLON = ":";

    public static final String LINE_BREAK = "\n";

    public static final String POUND = "#";

    /**
     * second.
     */
    public static final int ASYNC_UPDATE_ADDRESS_INTERVAL = 300;

    /**
     * second.
     */
    public static final int POLLING_INTERVAL_TIME = 15;

    /**
     * millisecond.
     */
    public static final int ONCE_TIMEOUT = 2000;

    /**
     * millisecond.
     */
    public static final int SO_TIMEOUT = 60000;

    /**
     * millisecond.
     */
    public static final int CONFIG_LONG_POLL_TIMEOUT = 30000;

    /**
     * millisecond.
     */
    public static final int MIN_CONFIG_LONG_POLL_TIMEOUT = 10000;

    /**
     * millisecond.
     */
    public static final int CONFIG_RETRY_TIME = 2000;

    /**
     * Maximum number of retries.
     */
    public static final int MAX_RETRY = 3;

    /**
     * millisecond.
     */
    public static final int RECV_WAIT_TIMEOUT = ONCE_TIMEOUT * 5;

    public static final String ENCODE = "UTF-8";

    public static final String APPNAME = "AppName";

    public static final String CLIENT_APPNAME_HEADER = "Client-AppName";

    public static final String CLIENT_REQUEST_TS_HEADER = "Client-RequestTS";

    public static final String CLIENT_REQUEST_TOKEN_HEADER = "Client-RequestToken";

    public static final String VIPSERVER_TAG = "Vipserver-Tag";

    public static final String AMORY_TAG = "Amory-Tag";

    public static final String LOCATION_TAG = "Location-Tag";

    public static final String CHARSET_KEY = "charset";

    public static final String CLUSTER_NAME_PATTERN_STRING = "^[0-9a-zA-Z-]+$";

    /**
     * The constants in remote directory.
     */
    public static class Remote {

        public static final String INTERNAL_MODULE = "internal";
    }

    /**
     * The constants in exception directory.
     */
    public static class Exception {

        public static final int DESERIALIZE_ERROR_CODE = 101;

        public static final int SERIALIZE_ERROR_CODE = 100;

        public static final int COMMON_ERROR_CODE = 99;
    }
}
