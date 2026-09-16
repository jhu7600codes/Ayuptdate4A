package com.exteragram.messenger;

import android.content.Context;
import android.content.SharedPreferences;

import com.exteragram.messenger.utils.chats.DoubleTapUtils;
import com.exteragram.messenger.utils.text.TranslatorUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.SharedConfig;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class ExteraConfig {
    public static boolean addCommaAfterMention;
    public static boolean alwaysSendInHD;
    public static boolean archiveOnPull;
    public static float avatarCorners;
    public static int bottomButton;
    public static boolean cameraMirrorMode;
    public static boolean cameraStabilization;
    public static int cameraType;
    public static boolean centerTitle;
    public static boolean checkUpdatesOnLaunch;
    private static boolean configLoaded;
    public static boolean customThemes;
    public static boolean disableDividers;
    public static boolean disableGreetingSticker;
    public static boolean disableNumberRounding;
    public static boolean disableUnarchiveSwipe;
    public static ArrayList<String> doNotMarkAsNew;
    public static boolean doNotUseProxyWithVpn;
    public static int doubleTapAction;
    public static int doubleTapActionOutOwner;
    public static int doubleTapSeekDuration;
    public static int downloadSpeedBoost;
    public static String editingIconPackId;
    public static SharedPreferences.Editor editor;
    public static boolean enableAdBlock;
    public static int eventType;
    public static boolean extendedFramesPerSecond;
    public static boolean filterZalgo;
    public static float flashIntensity;
    public static float flashWarmth;
    public static boolean forceBlur;
    public static boolean forceSnow;
    public static boolean formatTimeWithSeconds;
    public static boolean glareOnElements;
    public static boolean gooeyAvatarAnimation;
    public static boolean groupMessageMenu;
    public static boolean hideActionBarStatus;
    public static boolean hideAllChats;
    public static boolean hideArchiveFolder;
    public static boolean hideCameraTile;
    public static boolean hideFloatingButton;
    public static boolean hideKeyboardOnScroll;
    public static boolean hidePhoneNumber;
    public static boolean hidePhotoCounter;
    public static boolean hideReactionsInChannels;
    public static boolean hideReactionsInGroups;
    public static boolean hideReactionsInPrivateChats;
    public static boolean hideSendAsPeer;
    public static boolean hideShareButton;
    public static boolean hideStickerTime;
    public static boolean hideStories;
    public static int iconPack;
    public static boolean immersiveDrawerAnimation;
    public static boolean inAppVibration;
    public static boolean navigationDrawer;
    public static HashMap<String, Long> newFeaturesShowedAt;
    public static boolean newLoadingStyle;
    public static boolean newSliderStyle;
    public static boolean newSwitchStyle;
    public static boolean pauseOnMinimizeRound;
    public static boolean pauseOnMinimizeVideo;
    public static boolean pauseOnMinimizeVoice;
    public static boolean postprocessingWithAi;
    public static boolean predictiveBackAnimation;
    public static boolean preferOriginalQuality;
    public static SharedPreferences preferences;
    public static boolean quickAdminShortcuts;
    public static boolean quickTransitionForChannels;
    public static boolean quickTransitionForTopics;
    public static String recognitionLanguage;
    public static boolean relativeLastSeen;
    public static boolean rememberLastUsedCamera;
    public static boolean removeMessageTail;
    public static boolean replaceEditedWithIcon;
    public static boolean replyBackground;
    public static boolean replyColors;
    public static boolean replyEmoji;
    public static boolean sectionsSeparatedHeaders;
    public static boolean senderMiniAvatars;
    public static boolean showClearButton;
    public static boolean showCopyPhotoButton;
    public static boolean showDetailsButton;
    public static boolean showGenerateButton;
    public static boolean showHistoryButton;
    public static int showIdAndDc;
    public static boolean showOnlineStatus;
    public static boolean showRepeatMessageButton;
    public static boolean showReportButton;
    public static boolean showResultsBeforeVoting;
    public static boolean showSaveMessageButton;
    public static boolean singleCornerRadius;
    public static boolean springAnimations;
    public static boolean squareFab;
    public static boolean staticZoom;
    public static int stickerShape;
    public static float stickerSize;
    public static boolean swipeToPip;
    public static boolean tabCounter;
    public static int tabIcons;
    public static int tabletMode;
    public static String targetLang;
    public static int titleText;
    public static int translationFormality;
    public static int translationProvider;
    public static boolean unlimitedRecentStickers;
    public static boolean unmuteWithVolumeButtons;
    public static long updateScheduleTimestamp;
    public static boolean uploadSpeedBoost;
    public static boolean useGoogleAnalytics;
    public static boolean useGoogleCrashlytics;
    public static boolean useSystemFonts;
    public static boolean useSystemIconShape;
    public static boolean useYandexMaps;
    public static int videoMessagesCamera;
    public static int voiceHintShowcases;

    public static final Gson GSON = new Gson();
    private static final Object sync = new Object();
    public static ArrayList<Integer> mainMenuLayout = new ArrayList<>();
    public static ArrayList<Integer> mainMenuHiddenItems = new ArrayList<>();
    public static ArrayList<String> iconPacksLayout = new ArrayList<>();
    public static ArrayList<String> iconPacksHidden = new ArrayList<>();

    private static final String[] BASE_ICON_PACKS = {"base.default", "base.solar", "base.remix"};

    static {
        loadConfig();
    }

    public enum MainMenuItem {
        GHOST_MODE(200),
        KILL_APP(201),
        DIVIDER(-1),
        PROFILE(18),
        ARCHIVE(14),
        BOTS(105),
        NEW_GROUP(2),
        CONTACTS(6),
        NEW_CHANNEL(3),
        CALLS(10),
        SAVED(11),
        SETTINGS(8),
        BROWSER(101),
        QR(17);

        public final int id;

        MainMenuItem(int id) {
            this.id = id;
        }

        public static MainMenuItem getById(int id) {
            for (MainMenuItem item : values()) {
                if (item.id == id) {
                    return item;
                }
            }
            return null;
        }
    }

    public static final class BottomNavigationBar {
        private static int mode;

        public static int getMode() {
            if (mode < 0 || mode > 2) {
                mode = 0;
            }
            return mode;
        }

        public static void setMode(int newMode) {
            mode = newMode;
            getMode();
        }

        public static boolean hidden() {
            return getMode() == 1;
        }

        public static boolean visible() {
            return getMode() != 1;
        }

        public static boolean floating() {
            return getMode() == 2;
        }
    }

    public static void ensureSettingsVisibility() {
        if (BottomNavigationBar.hidden()) {
            Integer settings = MainMenuItem.SETTINGS.id;
            if (mainMenuLayout.contains(settings)) {
                return;
            }
            mainMenuHiddenItems.remove(settings);
            mainMenuLayout.add(settings);
            saveMainMenuLayout();
        }
    }

    public static void sanitizeMenu() {
        boolean changed = mainMenuLayout.removeIf(id -> id != MainMenuItem.DIVIDER.id && MainMenuItem.getById(id) == null)
                | mainMenuHiddenItems.removeIf(id -> id != MainMenuItem.DIVIDER.id && MainMenuItem.getById(id) == null);
        for (MainMenuItem item : MainMenuItem.values()) {
            if (item != MainMenuItem.DIVIDER && !mainMenuLayout.contains(item.id) && !mainMenuHiddenItems.contains(item.id)) {
                mainMenuHiddenItems.add(item.id);
                changed = true;
            }
        }
        if (changed) {
            saveMainMenuLayout();
        }
    }

    public static ArrayList<Integer> getDefaultMainMenuLayout() {
        ArrayList<Integer> layout = new ArrayList<>();
        layout.add(MainMenuItem.GHOST_MODE.id);
        layout.add(MainMenuItem.DIVIDER.id);
        layout.add(MainMenuItem.ARCHIVE.id);
        if (BottomNavigationBar.hidden()) {
            layout.add(MainMenuItem.PROFILE.id);
        }
        layout.add(MainMenuItem.NEW_GROUP.id);
        if (BottomNavigationBar.hidden()) {
            layout.add(MainMenuItem.CONTACTS.id);
        }
        layout.add(MainMenuItem.SAVED.id);
        layout.add(MainMenuItem.BOTS.id);
        if (BottomNavigationBar.hidden()) {
            layout.add(MainMenuItem.SETTINGS.id);
        }
        return layout;
    }

    public static void loadConfig() {
        synchronized (sync) {
            if (configLoaded) {
                return;
            }
            preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Context.MODE_PRIVATE);
            editor = preferences.edit();

            cameraType = preferences.getInt("cameraType", getPerformanceClassFast() == 2 ? 2 : 0);
            translationProvider = preferences.getInt("translationProvider", 0);
            translationFormality = preferences.getInt("translationFormality", 0);
            disableNumberRounding = preferences.getBoolean("disableNumberRounding", false);
            formatTimeWithSeconds = preferences.getBoolean("formatTimeWithSeconds", false);
            relativeLastSeen = preferences.getBoolean("relativeLastSeen", false);
            inAppVibration = preferences.getBoolean("inAppVibration", true);
            filterZalgo = preferences.getBoolean("filterZalgo", true);
            tabletMode = preferences.getInt("tabletMode", 0);
            downloadSpeedBoost = preferences.getInt("downloadSpeedBoost", 0);
            uploadSpeedBoost = preferences.getBoolean("uploadSpeedBoost", false);
            hidePhoneNumber = preferences.getBoolean("hidePhoneNumber", false);
            showIdAndDc = preferences.getInt("showIdAndDc", 2);
            hideArchiveFolder = preferences.getBoolean("hideArchiveFolder", false);
            archiveOnPull = preferences.getBoolean("archiveOnPull", false);
            disableUnarchiveSwipe = preferences.getBoolean("disableUnarchiveSwipe", true);
            useYandexMaps = preferences.getBoolean("useYandexMaps", false);
            enableAdBlock = preferences.getBoolean("enableAdBlock", true);
            doNotUseProxyWithVpn = preferences.getBoolean("doNotUseProxyWithVpn", false);
            avatarCorners = preferences.getFloat("avatarCorners", 28.0f);
            singleCornerRadius = preferences.getBoolean("singleCornerRadius", false);
            hideActionBarStatus = preferences.getBoolean("hideActionBarStatus", false);
            hideStories = preferences.getBoolean("hideStories", false);
            centerTitle = preferences.getBoolean("centerTitle", false);
            hideFloatingButton = preferences.getBoolean("hideFloatingButton", false);
            BottomNavigationBar.setMode(preferences.getInt("bottomNavigationBarMode", 0));
            senderMiniAvatars = preferences.getBoolean("senderMiniAvatars", false);
            titleText = preferences.getInt("titleText", 2);
            tabCounter = preferences.getBoolean("tabCounter", true);
            tabIcons = preferences.getInt("tabIcons", 1);
            hideAllChats = preferences.getBoolean("hideAllChats", false);
            iconPack = preferences.getInt("iconPack", 1);
            editingIconPackId = preferences.getString("editingIconPackId", null);
            squareFab = preferences.getBoolean("squareFab", true);
            sectionsSeparatedHeaders = preferences.getBoolean("sectionsSeparatedHeaders", true);
            disableDividers = preferences.getBoolean("disableDividers", false);
            newLoadingStyle = preferences.getBoolean("newLoadingStyle", true);
            newSliderStyle = preferences.getBoolean("newSliderStyle", true);
            forceSnow = preferences.getBoolean("forceSnow", false);
            useSystemFonts = preferences.getBoolean("useSystemFonts", true);
            newSwitchStyle = preferences.getBoolean("newSwitchStyle", true);
            removeMessageTail = preferences.getBoolean("removeMessageTail", true);
            gooeyAvatarAnimation = preferences.getBoolean("gooeyAvatarAnimation", true);
            predictiveBackAnimation = preferences.getBoolean("predictiveBackAnimation", true);
            springAnimations = preferences.getBoolean("springAnimations", true);
            glareOnElements = preferences.getBoolean("glareOnElements", true);
            forceBlur = preferences.getBoolean("forceBlur", false);
            eventType = preferences.getInt("eventType", 0);
            navigationDrawer = preferences.getBoolean("navigationDrawer", false);
            immersiveDrawerAnimation = preferences.getBoolean("immersiveDrawerAnimation", false);

            String packsLayout = preferences.getString("iconPacksLayout", null);
            String packsHidden = preferences.getString("iconPacksHidden", null);
            if (packsLayout != null) {
                iconPacksLayout = GSON.fromJson(packsLayout, new TypeToken<ArrayList<String>>() {}.getType());
                iconPacksHidden = packsHidden != null
                        ? GSON.fromJson(packsHidden, new TypeToken<ArrayList<String>>() {}.getType())
                        : new ArrayList<>();
            } else {
                iconPacksLayout = new ArrayList<>();
                iconPacksHidden = new ArrayList<>();
                for (int i = 0; i < BASE_ICON_PACKS.length; i++) {
                    (i == iconPack ? iconPacksLayout : iconPacksHidden).add(BASE_ICON_PACKS[i]);
                }
                saveIconPacksLayout();
            }
            if (iconPacksLayout == null) iconPacksLayout = new ArrayList<>();
            if (iconPacksHidden == null) iconPacksHidden = new ArrayList<>();

            String notNew = preferences.getString("doNotMarkAsNew", null);
            doNotMarkAsNew = notNew != null
                    ? GSON.fromJson(notNew, new TypeToken<ArrayList<String>>() {}.getType())
                    : new ArrayList<>();
            if (doNotMarkAsNew == null) doNotMarkAsNew = new ArrayList<>();

            String showedAt = preferences.getString("newFeaturesShowedAt", null);
            newFeaturesShowedAt = showedAt != null
                    ? GSON.fromJson(showedAt, new TypeToken<HashMap<String, Long>>() {}.getType())
                    : new HashMap<>();
            if (newFeaturesShowedAt == null) newFeaturesShowedAt = new HashMap<>();

            boolean packsChanged = false;
            for (String pack : BASE_ICON_PACKS) {
                if (!iconPacksLayout.contains(pack) && !iconPacksHidden.contains(pack)) {
                    iconPacksHidden.add(pack);
                    packsChanged = true;
                }
            }
            if (packsChanged) {
                saveIconPacksLayout();
            }

            String menuLayout = preferences.getString("mainMenuLayout", null);
            String menuHidden = preferences.getString("mainMenuHiddenItems", null);
            if (menuLayout != null) {
                mainMenuLayout = GSON.fromJson(menuLayout, new TypeToken<ArrayList<Integer>>() {}.getType());
                mainMenuHiddenItems = menuHidden != null
                        ? GSON.fromJson(menuHidden, new TypeToken<ArrayList<Integer>>() {}.getType())
                        : new ArrayList<>();
                if (mainMenuLayout == null) mainMenuLayout = new ArrayList<>();
                if (mainMenuHiddenItems == null) mainMenuHiddenItems = new ArrayList<>();
            } else {
                mainMenuLayout = new ArrayList<>(getDefaultMainMenuLayout());
                mainMenuHiddenItems = new ArrayList<>();
                for (MainMenuItem item : MainMenuItem.values()) {
                    if (item != MainMenuItem.DIVIDER && !mainMenuLayout.contains(item.id)) {
                        mainMenuHiddenItems.add(item.id);
                    }
                }
                saveMainMenuLayout();
            }
            mainMenuLayout.removeAll(mainMenuHiddenItems);
            ensureSettingsVisibility();
            sanitizeMenu();

            stickerSize = preferences.getFloat("stickerSize", 12.0f);
            stickerShape = preferences.getInt("stickerShape", 1);
            replyColors = preferences.getBoolean("replyColors", true);
            replyEmoji = preferences.getBoolean("replyEmoji", true);
            replyBackground = preferences.getBoolean("replyBackground", true);
            hideStickerTime = preferences.getBoolean("hideStickerTime", false);
            unlimitedRecentStickers = preferences.getBoolean("unlimitedRecentStickers", false);
            hideSendAsPeer = preferences.getBoolean("hideSendAsPeer", false);
            hideReactionsInPrivateChats = preferences.getBoolean("hideReactionsInPrivateChats", false);
            hideReactionsInGroups = preferences.getBoolean("hideReactionsInGroups", false);
            hideReactionsInChannels = preferences.getBoolean("hideReactionsInChannels", false);
            doubleTapAction = DoubleTapUtils.sanitizeSetting(preferences.getInt("doubleTapAction", 1));
            doubleTapActionOutOwner = DoubleTapUtils.sanitizeSetting(preferences.getInt("doubleTapActionOutOwner", 1));
            bottomButton = preferences.getInt("bottomButton", 2);
            hideKeyboardOnScroll = preferences.getBoolean("hideKeyboardOnScroll", true);
            quickAdminShortcuts = preferences.getBoolean("quickAdminShortcuts", true);
            quickTransitionForChannels = preferences.getBoolean("quickTransitionForChannels", true);
            quickTransitionForTopics = preferences.getBoolean("quickTransitionForTopics", true);
            disableGreetingSticker = preferences.getBoolean("disableGreetingSticker", false);
            hideShareButton = preferences.getBoolean("hideShareButton", true);
            showResultsBeforeVoting = preferences.getBoolean("showResultsBeforeVoting", false);
            showOnlineStatus = preferences.getBoolean("showOnlineStatus", false);
            replaceEditedWithIcon = preferences.getBoolean("replaceEditedWithIcon", true);
            showDetailsButton = preferences.getBoolean("showDetailsButton", true);
            showGenerateButton = preferences.getBoolean("showGenerateButton", true);
            showSaveMessageButton = preferences.getBoolean("showSaveMessageButton", false);
            showRepeatMessageButton = preferences.getBoolean("showRepeatMessageButton", false);
            showCopyPhotoButton = preferences.getBoolean("showCopyPhotoButton", true);
            showClearButton = preferences.getBoolean("showClearButton", true);
            showReportButton = preferences.getBoolean("showReportButton", true);
            showHistoryButton = preferences.getBoolean("showHistoryButton", false);
            groupMessageMenu = preferences.getBoolean("groupMessageMenu", true);
            customThemes = preferences.getBoolean("customThemes", true);
            addCommaAfterMention = preferences.getBoolean("addCommaAfterMention", true);
            hidePhotoCounter = preferences.getBoolean("hidePhotoCounter", true);
            alwaysSendInHD = preferences.getBoolean("alwaysSendInHD", true);
            hideCameraTile = preferences.getBoolean("hideCameraTile", true);
            recognitionLanguage = preferences.getString("recognitionLanguage", "none");
            postprocessingWithAi = preferences.getBoolean("postprocessingWithAi", false);
            extendedFramesPerSecond = preferences.getBoolean("extendedFramesPerSecond", false);
            cameraStabilization = preferences.getBoolean("cameraStabilization", false);
            cameraMirrorMode = preferences.getBoolean("cameraMirrorMode", true);
            staticZoom = preferences.getBoolean("staticZoom", false);
            videoMessagesCamera = preferences.getInt("videoMessagesCamera", 0);
            rememberLastUsedCamera = preferences.getBoolean("rememberLastUsedCamera", false);
            pauseOnMinimizeVideo = preferences.getBoolean("pauseOnMinimizeVideo", true);
            pauseOnMinimizeVoice = preferences.getBoolean("pauseOnMinimizeVoice", false);
            pauseOnMinimizeRound = preferences.getBoolean("pauseOnMinimizeRound", false);
            doubleTapSeekDuration = preferences.getInt("doubleTapSeekDuration", 1);
            preferOriginalQuality = preferences.getBoolean("preferOriginalQuality", false);
            swipeToPip = preferences.getBoolean("swipeToPip", false);
            unmuteWithVolumeButtons = preferences.getBoolean("unmuteWithVolumeButtons", false);
            updateScheduleTimestamp = preferences.getLong("updateScheduleTimestamp", 0L);
            checkUpdatesOnLaunch = preferences.getBoolean("checkUpdatesOnLaunch", true);
            targetLang = preferences.getString("targetLang", "app");
            TranslatorUtils.ensureTargetLanguageCompatibleWithProvider();
            voiceHintShowcases = preferences.getInt("voiceHintShowcases", 0);
            useGoogleCrashlytics = preferences.getBoolean("useGoogleCrashlytics", false);
            useGoogleAnalytics = preferences.getBoolean("useGoogleAnalytics", false);
            flashWarmth = preferences.getFloat("flashWarmth", 0.5f);
            flashIntensity = preferences.getFloat("flashIntensity", 1.0f);
            useSystemIconShape = preferences.getBoolean("useSystemIconShape", true);
            configLoaded = true;
        }
    }

    private static int getPerformanceClassFast() {
        return ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Context.MODE_PRIVATE)
                .getInt("devicePerformanceClass", SharedConfig.getDevicePerformanceClass());
    }

    public static int getAvatarCorners(float size) {
        return getAvatarCorners(size, false, false, false);
    }

    public static int getAvatarCorners(float size, boolean raw) {
        return getAvatarCorners(size, raw, false, false);
    }

    public static int getAvatarCorners(float size, boolean raw, boolean inner) {
        return getAvatarCorners(size, raw, inner, false);
    }

    public static int getAvatarCorners(float size, boolean raw, boolean inner, boolean reduced) {
        if (avatarCorners == 0.0f) {
            return 0;
        }
        float radius = (avatarCorners * size) / 56.0f;
        if (reduced) {
            radius -= 2.5f;
        }
        if (!raw) {
            radius = AndroidUtilities.dp(radius);
        }
        if (inner && !singleCornerRadius) {
            radius = (((int) radius) * 42) >> 6;
        }
        return (int) Math.ceil(radius);
    }

    public static float getAvatarSquareness() {
        float squareness = 1.0f - (avatarCorners / 28.0f);
        if (squareness < 0.0f) {
            return 0.0f;
        }
        return Math.min(squareness, 1.0f);
    }

    public static int getOnlineDotOuterRadius() {
        return AndroidUtilities.dp((getAvatarSquareness() * 2.0f) + 7.0f);
    }

    public static int getOnlineDotInnerRadius() {
        return AndroidUtilities.dp(getAvatarSquareness() + 5.0f);
    }

    public static float getOnlineDotOffset(float circleOffset, float size) {
        return circleOffset + ((((float) (size / Math.sqrt(2.0d))) - circleOffset) * getAvatarSquareness());
    }

    public static void toggleLogging() {
        setLogging(!BuildVars.LOGS_ENABLED);
    }

    public static boolean getLogging() {
        return ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", Context.MODE_PRIVATE).getBoolean("logsEnabled", false);
    }

    public static void setLogging(boolean enabled) {
        BuildVars.LOGS_ENABLED = enabled;
        ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", Context.MODE_PRIVATE).edit().putBoolean("logsEnabled", enabled).apply();
        if (!BuildVars.LOGS_ENABLED) {
            FileLog.cleanupLogs();
        }
    }

    public static String getCurrentLangName() {
        return TranslatorUtils.getTargetLanguageTitle();
    }

    public static int getDoubleTapSeekDuration() {
        if (doubleTapSeekDuration >= 0 && doubleTapSeekDuration <= 2) {
            return (doubleTapSeekDuration + 1) * 5000;
        }
        return 30000;
    }

    public static void reloadConfig() {
        synchronized (sync) {
            configLoaded = false;
            loadConfig();
        }
    }

    public static void init(boolean fromApplication) {
        // exteraGram initialised its remote API, plugin engine and ad-block here;
        // those subsystems are ported separately.
    }

    public static void saveMainMenuLayout() {
        editor.putString("mainMenuLayout", GSON.toJson(mainMenuLayout));
        editor.putString("mainMenuHiddenItems", GSON.toJson(mainMenuHiddenItems));
        editor.apply();
    }

    public static void saveIconPacksLayout() {
        editor.putString("iconPacksLayout", GSON.toJson(iconPacksLayout));
        editor.putString("iconPacksHidden", GSON.toJson(iconPacksHidden));
        editor.apply();
    }

    public static boolean canUseYandexMaps() {
        return false;
    }
}
