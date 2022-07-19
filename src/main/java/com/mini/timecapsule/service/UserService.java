package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.UserRepository;
import com.mini.timecapsule.dto.UserDTO;
import com.mini.timecapsule.model.QUser;
import com.mini.timecapsule.model.User;
import com.mini.timecapsule.utils.CustomWebUtils;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;
import java.util.TimeZone;

public class UserService {

    @Autowired
    private UserRepository userRepository;


    /**
     * 전체 User의 목록을 가져오는 메소드 / 관리자또는 통계화면에서 사용할 것으로 예상됨
     * @param payload
     * @param userDTO
     */
    public void getUserList(CustomWebUtils.Payload payload, UserDTO userDTO) {

        BooleanBuilder predicate = new BooleanBuilder();
        QUser qUser = QUser.user;

        if (userDTO.getId() != null) {
            predicate.and(qUser.id.eq(qUser.id));
        }

        if (userDTO.getName() != null) {
            predicate.and(qUser.name.eq(qUser.name));
        }

        if (userDTO.getCreatedAt() != null) {
            ZonedDateTime startDate = LocalDate.parse(userDTO.getCreatedAt(), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(TimeZone.getDefault().toZoneId());
            ZonedDateTime endDate = LocalDate.parse(userDTO.getCreatedAt(), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(TimeZone.getDefault().toZoneId()).plusDays(1);
            predicate.and(qUser.createdAt.between(startDate, endDate));
        }

        Iterable<User> users = userRepository.findAll(predicate);

        payload.addData("users", users);

    }

    /**
     * TODO : user을 찾지 못했을 때 예외를 어떻게 처리할지?
     * 일반적인 유저들이 로그인할 때 사용될 메소드
     * @param payload
     * @param userDTO
     */
    public void getUser(CustomWebUtils.Payload payload, UserDTO userDTO) {

        BooleanBuilder predicate = new BooleanBuilder();
        QUser qUser = QUser.user;

        if (userDTO.getId() != null) {
            predicate.and(qUser.id.eq(qUser.id));
        }

        if (userDTO.getName() != null) {
            predicate.and(qUser.name.eq(qUser.name));
        }

        Optional<User> user = userRepository.findOne(predicate);

        user.ifPresent(value -> payload.addData("user", value));
    }

    public void createUser(CustomWebUtils.Payload payload, UserDTO userDTO) {



        //좌표생성로직 추가
//
//        User user = User.joinUser("12345", "1234", "1234", "1234", "1234", "1234",
//                "1234");

//        userRepository.save(user);

        //생성 후 사용자의 좌표 표시

//        payload.addData("coordinates", user.getCoordinates());
    }

    /**
     * 새로운좌표를 생성하는 메서드
     * @return
     */
    private int createCoordinates() {
        Random random = new Random();
        int xCoordinates = random.nextInt(2000);
        int yCoordinates = random.nextInt(4000);


        return 0;
    }

    /**
     * User 삭제기능 / 관리자에 의해 일부만 사용될 것으로 생각됨
     * @param payload
     * @param userDTO
     */
    public void deleteUser(CustomWebUtils.Payload payload, UserDTO userDTO) {

//        User user = userRepository.findOne()

//        userRepository.delete(us);
    }

}
