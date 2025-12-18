import com.sailtrack.backend.entity.User;
import com.sailtrack.backend.entity.AttendanceRecord;
import com.sailtrack.backend.entity.LeaveRecord;

public class TestCompile {
    public static void main(String[] args) {
        User user = new User();
        user.setDepartmentId(1L);
        user.setRoleId(2L);
        
        AttendanceRecord record = new AttendanceRecord();
        record.setIsLate(true);
        record.setIsEarlyLeave(false);
        record.setEarlyLeaveMinutes(30);
        
        System.out.println("Compilation test passed");
    }
}