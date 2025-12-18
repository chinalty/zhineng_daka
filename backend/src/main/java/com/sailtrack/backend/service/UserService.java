package com.sailtrack.backend.service;

import com.sailtrack.backend.dto.ChangePasswordRequest;
import com.sailtrack.backend.dto.UpdateProfileRequest;
import com.sailtrack.backend.entity.User;
import com.sailtrack.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.sailtrack.backend.repository.AttendanceRecordRepository attendanceRecordRepository;
    private final com.sailtrack.backend.repository.LeaveRecordRepository leaveRecordRepository;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       com.sailtrack.backend.repository.AttendanceRecordRepository attendanceRecordRepository,
                       com.sailtrack.backend.repository.LeaveRecordRepository leaveRecordRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.leaveRecordRepository = leaveRecordRepository;
    }
    
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
    
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
    
    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        User user = getUserById(userId);
        
        // 检查用户名唯一性（排除自己）
        if (!user.getUsername().equals(request.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("用户名已存在");
            }
        }
        
        // 检查邮箱唯一性（排除自己）
        if (!user.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("邮箱已存在");
            }
        }
        
        user.setUsername(request.getUsername());
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        
        userRepository.save(user);
    }
    
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = getUserById(userId);
        
        // 验证当前密码
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("当前密码不正确");
        }
        
        // 验证新密码和确认密码
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("新密码和确认密码不一致");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    
    public Map<String, Object> getUserStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 总出勤天数（所有签到的天数，包括迟到、早退）
        long totalDays = attendanceRecordRepository.countTotalAttendanceDays(userId);
        
        // 迟到次数
        long lateDays = attendanceRecordRepository.countLateByUserId(userId);
        
        // 请假天数（已批准的请假）
        Double leaveDays = leaveRecordRepository.sumLeaveDaysByUserId(userId);
        
        // 工作时长（小时）
        Double workHours = attendanceRecordRepository.sumWorkHoursByUserId(userId);
        
        stats.put("totalDays", totalDays);
        stats.put("lateDays", lateDays);
        stats.put("leaveDays", leaveDays != null ? leaveDays : 0);
        stats.put("workHours", workHours != null ? Math.round(workHours * 10) / 10.0 : 0);
        
        return stats;
    }
    
    public List<User> getDepartmentUsers(Long departmentId) {
        return userRepository.findByDepartmentId(departmentId);
    }
    
    @Transactional
    public void updateFaceImage(Long userId, String faceImageUrl) {
        User user = getUserById(userId);
        user.setFaceImageUrl(faceImageUrl);
        userRepository.save(user);
    }
}