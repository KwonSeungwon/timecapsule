package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.CoordinatesRepository;
import com.mini.timecapsule.dao.UserRepository;
import com.mini.timecapsule.dto.UserDTO;
import com.mini.timecapsule.model.Coordinates;
import com.mini.timecapsule.model.QUser;
import com.mini.timecapsule.model.User;
import com.mini.timecapsule.utils.Payload;
import com.querydsl.core.BooleanBuilder;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TimeZone;

/**
 * 유저에 관한 모든기능들이 들어있는 서빗스
 * @author 카와이승원
 * @since 2022
 * TODO : Base64 암호화 API가 없어진다는데 뭐씀?
 */
@Service
@Log4j2
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CoordinatesRepository coordinatesRepository;


    /**
     * TODO : 로긴뿐만 아니라 세션에 관한 기능 구현필요..
     * @apiNote 암호화 되있는게 너무 많아서 존나괴로움
     * @param userDTO
     */
    public void login(UserDTO userDTO) {

        String coordinates = userDTO.getCoordinates();
        Base64 base64 = new Base64();
//        Optional<User> user = userRepository.findByCoordinates(base64.encode(coordinates.getBytes()));
//        user.ifPresent(value -> passwordEncoder.matches(userDTO.getPassword(), value.getPassword())); // user가 입력한 패쓰와드와 비교
    }

    /**
     * 전체 User의 목록을 가져오는 메소드 / 관리자또는 통계화면에서 사용할 것으로 예상됨
     * @param payload
     * @param userDTO
     */
    public void getUserList(Payload payload, UserDTO userDTO) {

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
    public void getUser(Payload payload, UserDTO userDTO) {

        BooleanBuilder predicate = new BooleanBuilder();
        QUser qUser = QUser.user;

        if (userDTO.getId() != null) {
            predicate.and(qUser.id.eq(qUser.id));
        }

        if (userDTO.getPassword() != null) {
            predicate.and(qUser.password.eq(qUser.password));
        }

        Optional<User> user = userRepository.findOne(predicate);

        user.ifPresent(value -> payload.addData("user", value));
    }

    public Map<String, String> createUser(Payload payload, UserDTO userDTO) {

//        String password = passwordEncoder.encode(userDTO.getPassword());
//        ZonedDateTime writeableAt = this.calculationWritingDays(userDTO.getOpenDayType());
//        List<Coordinates> coordinates = coordinatesRepository.findAll();
//        User user = User.joinUser(coordinates.get(0), password, userDTO.getName(), userDTO.getCapsuleType(),
//                userDTO.getOpenDayType(), writeableAt, null);
//
//        userRepository.save(user);

//        payload.addData("coordinates", user.getCoordinates());

//        payload.addData("coordinates", "test");
        Map<String, String> result = new HashMap<>();
        result.put("1234", "1234");
        return result;
    }

    /**
     * 작성가능 날짜는 임시로 오픈일로부터 3개월 전 까지만 가능하도록 계산함
     * @param openDay
     * @return
     */
    private ZonedDateTime calculationWritingDays(User.OpenDay openDay) {
        LocalDate renderOpenDay = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")) + openDay, DateTimeFormatter.ISO_LOCAL_DATE);
        return LocalDate.parse(renderOpenDay.toString(), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(TimeZone.getDefault().toZoneId()).minusMonths(1);
    }

    /**
     * 새로운좌표를 생성하는 메서드(임시로 Base64 암호화 / 암호화에 큰 의미는 없는듯?)
     * @return
     */
    private Coordinates encodeCoordinates(Coordinates coordinates) {

        Base64 base64 = new Base64();
        coordinates.setXCoordinates(Arrays.toString(base64.encode(coordinates.getXCoordinates().getBytes())));
        coordinates.setYCoordinates(Arrays.toString(base64.encode(coordinates.getYCoordinates().getBytes())));

        return coordinates;
    }

    private Coordinates decodeCoordinates(Coordinates coordinates) {

        Base64 base64 = new Base64();
        coordinates.setXCoordinates(Arrays.toString(base64.decode(coordinates.getXCoordinates().getBytes())));
        coordinates.setYCoordinates(Arrays.toString(base64.decode(coordinates.getYCoordinates().getBytes())));

        return coordinates;
    }

    /**
     * TODO: 통계학과 zl존Natural킹왕짱세젤예초미녀 "조현수" 님이 x와 y의 값의 최대값이 999일때 나올 수 있는 경우의 수를 구해줄 것임..
     */
    public void createRandomCoordinates() {

        Random random = new Random();
        int xCoordinates = random.nextInt(999);
        int yCoordinates = random.nextInt(999);

    }

    /**
     * TODO : 미리 좌표를 할당해놔서
     *
     */
    public void createAutoCoordinates() {

    }



    /**
     * User 삭제기능 / 관리자에 의해 일부만 사용될 것으로 생각됨
     * @param payload
     * @param userDTO
     */
    public void deleteUser(Payload payload, UserDTO userDTO) {

//        User user = userRepository.findOne()

//        userRepository.delete(us);
    }

}
