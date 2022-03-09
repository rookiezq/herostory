package com.rookied.herostory;

import java.util.Objects;

/**
 * 用户
 *
 * @author rookied
 * @date 2022.03.08
 */
public class User {
    /**
     * 用户 Id
     */
    public int userId;

    /**
     * 影响形象
     */
    public String heroAvatar;

    public User() {
    }

    public User(int userId, String heroAvatar) {
        this.userId = userId;
        this.heroAvatar = heroAvatar;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getHeroAvatar() {
        return heroAvatar;
    }

    public void setHeroAvatar(String heroAvatar) {
        this.heroAvatar = heroAvatar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId && Objects.equals(heroAvatar, user.heroAvatar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, heroAvatar);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", heroAvatar='" + heroAvatar + '\'' +
                '}';
    }


    public static void main(String[] args) {
        System.out.println(new User(1, "a").equals(new User(1, "a")));
    }
}
