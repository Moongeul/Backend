package com.core.book.api.book.constant;

import lombok.Getter;

@Getter
public enum BookTag {
    FAMILY(1, "가족"),
    FRIENDSHIP(2, "우정"),
    GRATITUDE(3, "감사"),
    RESPECT(4, "존경"),
    HAPPINESS(5, "행복"),
    FUN(6, "재미"),
    SELF_ESTEEM(7, "자존감"),
    HOPEFUL(8, "희망찬"),
    LOVE(9, "사랑"),
    WARMTH(10, "따뜻함"),
    PEACEFUL(11, "평온함"),
    EXCITING(12, "흥미진진"),
    PASSIONATE(13, "열정적"),
    TOUCHING(14, "감동적"),
    SADNESS(15, "슬픔"),
    ANGER(16, "화남"),
    DEPRESSION(17, "우울함"),
    ANNOYED(18, "짜증남"),
    DESPAIR(19, "절망적"),
    DISGUST(20, "혐오"),
    DIFFICULTY(21, "어려움"),
    ADULT(22, "19금"),
    EXCITED(23, "흥분됨"),
    TWIST(24, "반전"),
    HORROR(25, "공포"),
    DARK(26, "어두운"),
    CRUEL(27, "잔인한"),
    CHILDISH(28, "유치한"),
    BORING(29, "지루한"),
    DIRTY(30, "더러운");

    private final int id;
    private final String description;

    BookTag(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public static BookTag fromId(int id) {
        for (BookTag tag : values()) {
            if (tag.getId() == id) {
                return tag;
            }
        }
        throw new IllegalArgumentException("Invalid id: " + id);
    }

    public static BookTag fromDescription(String description) {
        for (BookTag tag : values()) {
            if (tag.getDescription().equals(description)) {
                return tag;
            }
        }
        throw new IllegalArgumentException("Invalid description: " + description);
    }
}