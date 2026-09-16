package com.exteragram.messenger.badges;

import com.exteragram.messenger.api.dto.BadgeDTO;

import org.telegram.tgnet.TLRPC;

import java.util.function.Consumer;

// exteraGram's supporter badges come from its own backend; this port has none,
// so nobody has a badge and badges cannot be changed.
public class BadgesController {
    public static final BadgesController INSTANCE = new BadgesController();

    public BadgeDTO getBadge(TLRPC.User user) {
        return null;
    }

    public BadgeDTO getDefaultBadge() {
        return null;
    }

    public boolean canChangeBadge() {
        return false;
    }

    public boolean isDeveloper(TLRPC.User user) {
        return false;
    }

    public boolean isDeveloper() {
        return false;
    }

    public void updateBadge(BadgeDTO badge, Consumer<String> callback) {
        if (callback != null) {
            callback.accept(null);
        }
    }
}
