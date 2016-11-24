package com.framgia.ishipper.util;

/**
 * Created by vuduychuong1994 on 8/3/16.
 */
public class Const {
    public static final String KEY_USER = "user";
    public static final int REQUEST_CHECK_SETTINGS = 2;
    public static final int REQUEST_SETTING = 3;
    public static final long TIME_DELAY_EXIT = 2000;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int PIN_LENGTH = 8;
    public static final int SETTING_MAX_INVOICE_RADIUS = 14;
    public static final int SETTING_MIN_INVOICE_RADIUS = 1;
    public static final int SETTING_INVOICE_RADIUS_DEFAULT = 5;
    public static final String ACTION_NEW_INVOICE = "new_invoice";
    public static final String ACTION_REMOVE_INVOICE = "remove_invoice";
    public static String VIETNAM_PREFIX = "+84";
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    public static final int ZERO = 0;
    public static final String WELCOME = "welcome";
    public static final String CHANNEL = "channel";
    public static final String CHANNEL_REALTIME = "RealtimeChannel";
    public static final String AUTHENTICATION_TOKEN = "authentication_token";
    public static final String COMMAND = "command";
    public static final String COMMAND_SUBSCRIBE = "subscribe";
    public static final String IDENTIFIER = "identifier";
    public static final String ACTION_UNREAD_NOTIFICATION = "unread_notification";
    public static final int SIZE_INVOICE_STATUS = 6;
    public static final String KEY_INVOICE_ID = "invoice_id";

    public class RequestCode {

        public static final int REQUEST_SEARCH_BLACKLIST = 1;
        public static final int REQUEST_SEARCH_FAVORITE = 2;
        public static final int REQUEST_CODE_CHOOSE_SHIPPER = 3;
        public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    }

    public class ErrorMessage {
        public static final String ERROR_MSG_LOCAL =
                "Có lỗi xảy ra trong quá trình kết nối, hãy thử lại!";
    }

    public class Firebase {
        public static final String TOPIC_NEWS = "news";
    }

    public class Storage {
        public static final String KEY_USER_INFO = "UserStorageKey";
        public static final String KEY_IS_LOGIN = "IsLoginKey";
        public static final String KEY_SETTING_NOTIFICATION = "SettingNotification";
        public static final String KEY_SETTING_INVOICE_RADIUS = "SettingInvoiceRadius";
    }

    public class LocationRequest {
        public static final int LOCATION_INTERVAL_UPDATE = 10000;
        public static final int LOCATION_FASTEST_INTERVAL_UPDATE = 5000;
    }

    public class MapPadding {
        public static final int TOP_PADDING = 150;
        public static final int RIGHT_PADDING = 0;
        public static final int BOTTOM_PADDING = 0;
        public static final int LEFT_PADDING = 0;
    }

    public class Notification {
        public static final int ON = 1;
        public static final int OFF = 0;
    }

    public class FirebaseData {
        public static final String INVOICE_ID = "invoice_id";

        public static final String NOTI_ID = "id";
    }

    public class Setting {
        public static final int PER_PAGE = 12;
    }

    public class Broadcast {
        public static final String NEW_NOTIFICATION_ACTION = "com.framgia.ishipper.NEW_NOTIFICATION_ACTION";
    }

    public class ElevationLevel {
        public static final int DEFAULT = 8;
        public static final int NONE = 0;
    }

    public class KeyIntent {
        public static final String KEY_INVOICE = "invoice";
    }

    public class Language {
        public static final String VIETNAMESE = "vi";
    }
}
