public enum State {

    ADMIN(1L),
    BASIC(2L);


    long roleId;

    State(long roleId) {
        this.roleId = roleId;
    }

    public long getRoleId() {
        return roleId;
    }
}