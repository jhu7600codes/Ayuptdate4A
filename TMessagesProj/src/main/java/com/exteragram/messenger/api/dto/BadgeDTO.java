package com.exteragram.messenger.api.dto;

import java.util.Objects;

public class BadgeDTO {
    private final long documentId;
    private final String text;

    public BadgeDTO(long documentId, String text) {
        this.documentId = documentId;
        this.text = text;
    }

    public long getDocumentId() {
        return documentId;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BadgeDTO)) return false;
        BadgeDTO other = (BadgeDTO) o;
        return documentId == other.documentId && Objects.equals(text, other.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentId, text);
    }
}
