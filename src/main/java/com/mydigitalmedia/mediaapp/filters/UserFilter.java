package com.mydigitalmedia.mediaapp.filters;

import com.mydigitalmedia.mediaapp.enums.UserRole;

public class UserFilter {

    String username;
    String password;
    UserRole userRole;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public static final class UserFilterBuilder {
        private String username;
        private String password;
        private UserRole userRole;

        private UserFilterBuilder() {
        }

        public static UserFilterBuilder anUserFilter() {
            return new UserFilterBuilder();
        }

        public UserFilterBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public UserFilterBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UserFilterBuilder withUserRole(UserRole userRole) {
            this.userRole = userRole;
            return this;
        }

        public UserFilter build() {
            UserFilter userFilter = new UserFilter();
            userFilter.password = this.password;
            userFilter.username = this.username;
            userFilter.userRole = this.userRole;
            return userFilter;
        }
    }
}
