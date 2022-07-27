package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.UserRepository;
import com.mini.timecapsule.dto.UserDTO;
import com.mini.timecapsule.model.QUser;
import com.mini.timecapsule.model.User;
import com.mini.timecapsule.utils.CustomWebUtils;
import com.querydsl.core.BooleanBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;
import java.util.TimeZone;

@Service
@Log4j2
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

        String coordinates = this.createCoordinates();
        ZonedDateTime writeableAt = this.calculationWritingDays(userDTO.getOpenDayType());
        User user = User.joinUser(coordinates, userDTO.getPassword(), userDTO.getName(), userDTO.getCapsuleType(),
                userDTO.getOpenDayType(), writeableAt, null);

        userRepository.save(user);

        FullCoordinate fullCoordinate = this.decodeCoordinates(user.getCoordinates());

        payload.addData("coordinates", fullCoordinate);
    }

    /**
     * TODO : Spring security 암호화 방법 확인필요
     * @param password
     * @return
     */
    private String passwordEncryption(String password) {


        return "";

    }

    /**
     * 작성가능 날짜는 임시로 오픈일로부터 3개월 전 까지만 가능하도록 계산함
     * @param openDay
     * @return
     */
    private ZonedDateTime calculationWritingDays(User.OpenDay openDay) {
        LocalDate renderOpenDay = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")) + openDay, DateTimeFormatter.ISO_LOCAL_DATE);
        return LocalDate.parse(renderOpenDay.toString(), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(TimeZone.getDefault().toZoneId()).minusMonths(3);
    }

    /**
     * 새로운좌표를 생성하는 메서드(임시로 Base64 암호화 / 암호화에 큰 의미는 없는듯?)
     * @return
     */
    private String createCoordinates() {
        Random random = new Random();
        int xCoordinates = random.nextInt(4000);
        int yCoordinates = random.nextInt(4000);
        String coordinates =  xCoordinates + "," + yCoordinates;
        BASE64Encoder base64Encoder = new BASE64Encoder();
        base64Encoder.encode(coordinates.getBytes());

        return coordinates;
    }

    private FullCoordinate decodeCoordinates(String coordinates) {
        FullCoordinate fullCoordinate = null;
        try {
            BASE64Decoder base64Decoder = new BASE64Decoder();
            String decodedCoordinates = new String(base64Decoder.decodeBuffer(String.valueOf(coordinates.getBytes())));
            String[] cutCoordinates =  decodedCoordinates.split(",");

            if (cutCoordinates.length > 1) {
                fullCoordinate = new FullCoordinate(cutCoordinates[0], cutCoordinates[1]);
            }
        } catch (Exception e) {
            log.error("decodeException!");
        }
        return fullCoordinate;
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
    

    @Getter
    @Setter
    static class FullCoordinate {
        private String x;
        private String y;
        public FullCoordinate(String x, String y) {
            this.x = x;
            this.y = y;
        }
    }
}
