public class TestSimple {
    public static void main(String[] args) {
        // 测试基本的getter/setter方法
        TestUser user = new TestUser();
        user.setDepartmentId(1L);
        user.setRoleId(2L);
        
        TestRecord record = new TestRecord();
        record.setIsLate(true);
        record.setIsEarlyLeave(false);
        record.setEarlyLeaveMinutes(30);
        
        System.out.println("Compilation test passed");
        System.out.println("User departmentId: " + user.getDepartmentId());
        System.out.println("User roleId: " + user.getRoleId());
        System.out.println("Record isLate: " + record.getIsLate());
        System.out.println("Record earlyLeaveMinutes: " + record.getEarlyLeaveMinutes());
    }
    
    static class TestUser {
        private Long departmentId;
        private Long roleId;
        
        public Long getDepartmentId() {
            return departmentId;
        }
        
        public void setDepartmentId(Long departmentId) {
            this.departmentId = departmentId;
        }
        
        public Long getRoleId() {
            return roleId;
        }
        
        public void setRoleId(Long roleId) {
            this.roleId = roleId;
        }
    }
    
    static class TestRecord {
        private Boolean isLate;
        private Boolean isEarlyLeave;
        private Integer earlyLeaveMinutes;
        
        public Boolean getIsLate() {
            return isLate;
        }
        
        public void setIsLate(Boolean isLate) {
            this.isLate = isLate;
        }
        
        public Boolean getIsEarlyLeave() {
            return isEarlyLeave;
        }
        
        public void setIsEarlyLeave(Boolean isEarlyLeave) {
            this.isEarlyLeave = isEarlyLeave;
        }
        
        public Integer getEarlyLeaveMinutes() {
            return earlyLeaveMinutes;
        }
        
        public void setEarlyLeaveMinutes(Integer earlyLeaveMinutes) {
            this.earlyLeaveMinutes = earlyLeaveMinutes;
        }
    }
}