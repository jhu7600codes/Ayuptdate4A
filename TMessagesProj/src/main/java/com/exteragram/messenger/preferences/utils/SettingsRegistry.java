package com.exteragram.messenger.preferences.utils;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import com.exteragram.messenger.preferences.GeneralPreferencesActivity;
import com.exteragram.messenger.preferences.MainPreferencesActivity;
import com.exteragram.messenger.preferences.OtherPreferencesActivity;
import com.exteragram.messenger.preferences.appearance.AppNavigationPreferencesActivity;
import com.exteragram.messenger.preferences.appearance.AppearancePreferencesActivity;
import com.exteragram.messenger.preferences.chats.ChatsPreferencesActivity;
import com.exteragram.messenger.preferences.utils.SettingsRegistry;
import com.google.gson.Gson;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatEditActivity;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ProfileActivity;

/* loaded from: classes.dex */
public class SettingsRegistry {
    private static final Map ayuCategories;
    private static final Map categoriesIcons;
    public static List newFeatures;
    private boolean entriesFetched;
    private final ConcurrentHashMap preparedEntries = new ConcurrentHashMap();
    private final ConcurrentHashMap entriesStringAlias = new ConcurrentHashMap();

    private static class SingletonHolder {
        private static final SettingsRegistry INSTANCE = new SettingsRegistry();
    }

    static {
        HashMap<Class<?>, Integer> icons = new HashMap<>();
        icons.put(MainPreferencesActivity.class, R.drawable.extera_outline);
        icons.put(GeneralPreferencesActivity.class, R.drawable.msg_media);
        icons.put(AppearancePreferencesActivity.class, R.drawable.msg_theme);
        icons.put(ChatsPreferencesActivity.class, R.drawable.msg_discussion);
        icons.put(OtherPreferencesActivity.class, R.drawable.msg_fave);
        icons.put(AppNavigationPreferencesActivity.class, R.drawable.msg_list);
        categoriesIcons = icons;
        ayuCategories = new HashMap<>();
        newFeatures = Arrays.asList("translationTargetLanguage", "relativeLastSeen", "hideArchiveFolder", "hideReactions", "disableGreetingSticker", "showOnlineStatus", "showResultsBeforeVoting", "Chats-MessageMenu-Repeat", "Camera-ExtendedSettings-SeamlessSwitching", "hideFloatingButton", "appNavigationSettings", "md3Styles", "predictiveBackAnimation", "bottomNavigationBarMode", "navigationDrawer");
    }

    public static SettingsRegistry getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static boolean isValidForSearch(UItem uItem) {
        if (uItem.id != 0 && !TextUtils.isEmpty(uItem.text)) {
            return true;
        }
        Integer valueOf = Integer.valueOf(uItem.id);
        Integer valueOf2 = Integer.valueOf(uItem.viewType);
        View view = uItem.view;
        FileLog.e(String.format("[Extera] UItems with ID 0 or empty text cannot be added as search result. (UItem ID: %s; View type: %s; View: %s; Text: %s; Subtext: %s)", valueOf, valueOf2, view == null ? null : view.getClass().getName(), uItem.text, TextUtils.concat(uItem.subtext, uItem.animatedText)));
        return false;
    }

    public static boolean isValidForLinkAliases(UItem uItem) {
        int i = uItem.id;
        if (i != 0) {
            return true;
        }
        Integer valueOf = Integer.valueOf(i);
        Integer valueOf2 = Integer.valueOf(uItem.viewType);
        View view = uItem.view;
        FileLog.e(String.format("[Extera] Cannot set link aliases for UItems with ID 0. (UItem ID: %s; View type: %s; View: %s; Text: %s; Subtext: %s)", valueOf, valueOf2, view == null ? null : view.getClass().getName(), uItem.text, TextUtils.concat(uItem.subtext, uItem.animatedText)));
        return false;
    }

    public void addSearchEntry(BaseFragment baseFragment, UItem uItem) {
        if (isValidForSearch(uItem)) {
            Entry fromUItem = Entry.fromUItem(baseFragment, uItem);
            if (!this.preparedEntries.containsKey(Integer.valueOf(generateGUIDForUItem(baseFragment.getClass(), uItem)))) {
                FileLog.d("[Extera] Added an entry: " + fromUItem);
            }
            this.preparedEntries.putIfAbsent(Integer.valueOf(fromUItem.guid), fromUItem);
        }
    }

    public static boolean markAsNewFeature(String str) {
        if (!newFeatures.contains(str) || ExteraConfig.doNotMarkAsNew.contains(str)) {
            return false;
        }
        Long l = (Long) ExteraConfig.newFeaturesShowedAt.get(str);
        if (l == null || l.longValue() == 0) {
            ExteraConfig.newFeaturesShowedAt.put(str, Long.valueOf(System.currentTimeMillis()));
            ExteraConfig.editor.putString("newFeaturesShowedAt", ExteraConfig.GSON.toJson(ExteraConfig.newFeaturesShowedAt)).apply();
            return true;
        }
        if (Math.abs(System.currentTimeMillis() - l.longValue()) <= TimeUnit.DAYS.toMillis(1L)) {
            return true;
        }
        ExteraConfig.newFeaturesShowedAt.remove(str);
        SharedPreferences.Editor editor = ExteraConfig.editor;
        Gson gson = ExteraConfig.GSON;
        editor.putString("newFeaturesShowedAt", gson.toJson(ExteraConfig.newFeaturesShowedAt));
        ExteraConfig.doNotMarkAsNew.add(str);
        ExteraConfig.editor.putString("doNotMarkAsNew", gson.toJson(ExteraConfig.doNotMarkAsNew)).apply();
        return false;
    }

    public void addLinkAliasForOption(String str, BaseFragment baseFragment, UItem uItem) {
        CharSequence charSequence;
        if (isValidForLinkAliases(uItem)) {
            if (markAsNewFeature(str) && (charSequence = uItem.text) != null && charSequence.length() > 0 && uItem.text.toString().charAt(uItem.text.toString().length() - 1) != 'd') {
                uItem.text = ChatEditActivity.applyNewSpan(uItem.text.toString());
            }
            if (this.entriesStringAlias.containsKey(str)) {
                FileLog.d("[Extera] Key '" + str + "' already linked to an entry.");
                return;
            }
            Entry entry = (Entry) this.preparedEntries.get(Integer.valueOf(generateGUIDForUItem(baseFragment.getClass(), uItem)));
            if (entry == null) {
                entry = Entry.fromUItem(baseFragment, uItem);
            }
            FileLog.d(String.format("[Extera] Added link alias %s for an entry %s", str, entry));
            this.entriesStringAlias.put(str, entry);
        }
    }

    public void handleLink(String str, String str2) {
        FileLog.d("[Extera] Setting link handler called with alias " + str);
        createEntriesIfNeeded();
        Entry entry = (Entry) this.entriesStringAlias.get(str);
        if (entry == null) {
            onSettingNotFound();
            return;
        }
        FileLog.d("[Extera] Found entry for alias: " + entry);
        FileLog.d("[Extera] Opening fragment...");
        openActivity(entry.fragmentClass, Integer.valueOf(entry.itemId));
    }

    public void onSettingNotFound() {
        onSettingNotFound(LaunchActivity.getLastFragment());
    }

    public void onSettingNotFound(BaseFragment baseFragment) {
        BulletinFactory.of(baseFragment).createEmojiBulletin("🤷\u200d♂️", LocaleController.getString(R.string.NoSuchSetting)).show();
    }

    public String getFirstSettingLink(Class cls, UItem uItem) {
        final int generateGUIDForUItem = generateGUIDForUItem(cls, uItem);
        Map.Entry entry = (Map.Entry) this.entriesStringAlias.entrySet().stream().filter(new Predicate() { // from class: com.exteragram.messenger.preferences.utils.SettingsRegistry$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return SettingsRegistry.$r8$lambda$p4BQB7CEiJF3IYJpUIO4QjqPKlE(generateGUIDForUItem, (Map.Entry) obj);
            }
        }).findFirst().orElse(null);
        if (entry == null) {
            return null;
        }
        return "https://t.me/" + (ayuCategories.containsKey(cls) ? "ayuSettings" : "exteraSettings") + "?s=" + ((String) entry.getKey());
    }

    public static /* synthetic */ boolean $r8$lambda$p4BQB7CEiJF3IYJpUIO4QjqPKlE(int i, Map.Entry entry) {
        return ((Entry) entry.getValue()).guid == i;
    }

    public ProfileActivity.SearchAdapter.SearchResult[] getSearchResults(final ProfileActivity.SearchAdapter searchAdapter) {
        createEntriesIfNeeded();
        return (ProfileActivity.SearchAdapter.SearchResult[]) this.preparedEntries.values().stream().map(new Function() { // from class: com.exteragram.messenger.preferences.utils.SettingsRegistry$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                ProfileActivity.SearchAdapter.SearchResult searchResult;
                searchResult = ((SettingsRegistry.Entry) obj).toSearchResult(searchAdapter);
                return searchResult;
            }
        }).toArray(new IntFunction() { // from class: com.exteragram.messenger.preferences.utils.SettingsRegistry$$ExternalSyntheticLambda4
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                return SettingsRegistry.$r8$lambda$Uq61hhP3TlTJmft55hT5WjpJsx0(i);
            }
        });
    }

    public static /* synthetic */ ProfileActivity.SearchAdapter.SearchResult[] $r8$lambda$Uq61hhP3TlTJmft55hT5WjpJsx0(int i) {
        return new ProfileActivity.SearchAdapter.SearchResult[i];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getCategoryIcon(Class cls) {
        return ((Integer) Objects.requireNonNullElse((Integer) categoriesIcons.get(cls), 0)).intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public BaseFragment initiateFragment(Class cls) {
        try {
            BaseFragment lastFragment = LaunchActivity.getLastFragment();
            if (lastFragment == null) {
                return null;
            }
            BaseFragment baseFragment = (BaseFragment) cls.getDeclaredConstructor(null).newInstance(null);
            baseFragment.setParentFragment(lastFragment);
            baseFragment.createActionBar(lastFragment.getContext());
            return baseFragment;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openActivity(Class cls, final Integer num) {
        final BaseFragment initiateFragment;
        final BaseFragment lastFragment = LaunchActivity.getLastFragment();
        if (lastFragment == null || (initiateFragment = initiateFragment(cls)) == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.preferences.utils.SettingsRegistry$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                lastFragment.presentFragment(initiateFragment);
            }
        });
        if (num == null || !(initiateFragment instanceof BasePreferencesActivity)) {
            return;
        }
        final BasePreferencesActivity basePreferencesActivity = (BasePreferencesActivity) initiateFragment;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.preferences.utils.SettingsRegistry$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                basePreferencesActivity.scrollToItem(num.intValue());
            }
        });
    }

    private void createEntriesIfNeeded() {
        if (this.entriesFetched) {
            return;
        }
        FileLog.d("[Extera] Initialising activities...");
        categoriesIcons.keySet().forEach(new Consumer() { // from class: com.exteragram.messenger.preferences.utils.SettingsRegistry$$ExternalSyntheticLambda7
            @Override // java.util.function.Consumer
            /* renamed from: accept */
            public final void accept(Object obj) {
                SettingsRegistry.this.initiateFragment((Class) obj);
            }
        });
        this.entriesFetched = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int generateGUIDForUItem(Class cls, UItem uItem) {
        return Objects.hash(cls.getName(), Integer.valueOf(uItem.id));
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class Entry {
        private final Class fragmentClass;
        private final int guid;
        private final int icon;
        private final int itemId;
        private final String subtext;
        private final String title;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry entry = (Entry) obj;
            return this.guid == entry.guid && this.itemId == entry.itemId && this.icon == entry.icon && Objects.equals(this.title, entry.title) && Objects.equals(this.subtext, entry.subtext) && Objects.equals(this.fragmentClass, entry.fragmentClass);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{Integer.valueOf(this.guid), Integer.valueOf(this.itemId), this.title, this.subtext, Integer.valueOf(this.icon), this.fragmentClass};
        }

        private Entry(int i, int i2, String str, String str2, int i3, Class cls) {
            this.guid = i;
            this.itemId = i2;
            this.title = str;
            this.subtext = str2;
            this.icon = i3;
            this.fragmentClass = cls;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public final int hashCode() {
            return SettingsRegistry$Entry$$ExternalSyntheticRecord0.m(this.guid, this.itemId, this.icon, this.title, this.subtext, this.fragmentClass);
        }

        public final String toString() {
            return "Entry";
        }

        public static Entry fromUItem(BaseFragment baseFragment, UItem uItem) {
            Class<?> cls = baseFragment.getClass();
            CharSequence charSequence = uItem.text;
            return new Entry(SettingsRegistry.generateGUIDForUItem(cls, uItem), uItem.id, charSequence == null ? null : String.valueOf(charSequence), baseFragment instanceof BasePreferencesActivity ? ((BasePreferencesActivity) baseFragment).getTitle() : null, SettingsRegistry.getInstance().getCategoryIcon(cls), cls);
        }

        public ProfileActivity.SearchAdapter.SearchResult toSearchResult(ProfileActivity.SearchAdapter searchAdapter) {
            Objects.requireNonNull(searchAdapter);
            return new ProfileActivity.SearchAdapter.SearchResult(this.guid, this.title, this.subtext, String.valueOf(this.itemId), this.icon, new Runnable() { // from class: com.exteragram.messenger.preferences.utils.SettingsRegistry$Entry$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    SettingsRegistry.Entry.this.lambda$toSearchResult$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$toSearchResult$0() {
            SettingsRegistry.getInstance().openActivity(this.fragmentClass, Integer.valueOf(this.itemId));
        }
    }
}
