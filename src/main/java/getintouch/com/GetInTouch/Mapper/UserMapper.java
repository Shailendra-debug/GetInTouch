package getintouch.com.GetInTouch.Mapper;

import getintouch.com.GetInTouch.DTO.Users.UserRegisterRequestDto;
import getintouch.com.GetInTouch.DTO.Users.UserResponseDto;
import getintouch.com.GetInTouch.Entity.User.User;

public class UserMapper {

    public static User toEntity(UserRegisterRequestDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        return user;
    }

    public static UserResponseDto toDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole().name());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
