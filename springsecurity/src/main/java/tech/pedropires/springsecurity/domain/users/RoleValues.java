package tech.pedropires.springsecurity.domain.users;

public enum RoleValues {

    ADMIN(1L),
    BASIC(2L);

    long roleId;

    RoleValues(long roleId) {
        this.roleId = roleId;
    }

    public long getRoleId() {
        return roleId;
    }
}
