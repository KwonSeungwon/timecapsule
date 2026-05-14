package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.CoordinatesRepository;
import com.mini.timecapsule.dao.UserRepository;
import com.mini.timecapsule.dto.UserDto;
import com.mini.timecapsule.model.Coordinates;
import com.mini.timecapsule.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final PasswordEncoder passwordEncoder;

    public Coordinates join() {
        return null;
    }

    @Transactional
    public void createUser(UserDto userDTO) {
        Optional<Coordinates> coordinate = coordinatesRepository.findByXCoordinatesAndYCoordinates(
                userDTO.getCoordinates().substring(0, 3),
                userDTO.getCoordinates().substring(3, 6)
        );

        if (!coordinate.isPresent()) {
            // 에러 처리
            return;
        }

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        User user = User.joinUser(
                coordinate.get(),
                encodedPassword,
                userDTO.getName(),
                userDTO.getEmail(),
                userDTO.getCapsuleType(),
                userDTO.getOpenDayType(),
                ZonedDateTime.now().plusMonths(1), // 기본 작성 가능일
                ""
        );

        userRepository.save(user);
        coordinate.get().fixCoordinatesAndUser(user);
    }

    public User getUser(UserDto userDTO) {
        return userRepository.findById(userDTO.getId()).orElse(null);
    }

    public List<User> getUserList(UserDto userDTO) {
        return userRepository.findAll();
    }
}
