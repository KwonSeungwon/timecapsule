package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.CoordinatesRepository;
import com.mini.timecapsule.dao.UserRepository;
import com.mini.timecapsule.dto.UserDto;
import com.mini.timecapsule.model.Coordinates;
import com.mini.timecapsule.model.QCoordinates;
import com.mini.timecapsule.model.QUser;
import com.mini.timecapsule.model.User;
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
import java.util.Optional;
import java.util.Random;
import java.util.TimeZone;

/**
 * 유저에 관한 모든기능들이 들어있는 서비스
 * @author Seungwon Kwon
 * @since 2022
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
     * 새로계정 만들기를 눌렀을 때 최초로 호출되는 메소드 / 좌표를 선점
     * TODO: 눌렀을 때 시점기준으로 좌표선점
     */
    public Coordinates join() {

        Random random = new Random();
        String xCoordinates = null;
        String yCoordinates = null;
        BooleanBuilder predicate = new BooleanBuilder();
        QCoordinates qCoordinates = QCoordinates.coordinates;
        while(true) {
            xCoordinates = String.valueOf(random.nextInt(999));
            yCoordinates = String.valueOf(random.nextInt(999));
            predicate.and(qCoordinates.xCoordinates.eq(xCoordinates));
            predicate.and(qCoordinates.yCoordinates.eq(yCoordinates));
            Optional<Coordinates> findCoordinates = coordinatesRepository.findOne(predicate);
            if (!findCoordinates.isPresent()) {
                break;
            }
            predicate = new BooleanBuilder();
        }
        Coordinates coordinates = new Coordinates().newCoordinates(xCoordinates, yCoordinates);
        coordinates.newCoordinates(xCoordinates, yCoordinates);
        coordinates.setIsFixed(true);
        coordinatesRepository.save(coordinates);//임시코드

        return coordinates;
    }

    /**
     * 전체 User의 목록을 가져오는 메소드 / 관리자또는 통계화면에서 사용할 것으로 예상됨
     * @param userDTO
     */
    public UserDto getUserList(UserDto userDTO) {

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

        return null;

    }

    /**
     * TODO : user을 찾지 못했을 때 예외를 어떻게 처리할지?
     * 일반적인 유저들이 로그인할 때 사용될 메소드
     * @param userDTO
     */
    public void getUser(UserDto userDTO) {

        BooleanBuilder predicate = new BooleanBuilder();
        QUser qUser = QUser.user;

        if (userDTO.getId() != null) {
            predicate.and(qUser.id.eq(qUser.id));
        }

        if (userDTO.getPassword() != null) {
            predicate.and(qUser.password.eq(qUser.password));
        }

        Optional<User> user = userRepository.findOne(predicate);

    }

    public void createUser(UserDto userDTO) {

        String[] coordinates = userDTO.getCoordinates().split(",");
        String password = passwordEncoder.encode(userDTO.getPassword());
        ZonedDateTime writeableAt = this.calculationWritingDays(userDTO.getOpenDayType());
        QCoordinates qCoordinates = QCoordinates.coordinates;
//        Optional<Coordinates> coordinate = coordinatesRepository.findOne(new BooleanBuilder().and(qCoordinates.xCoordinates.eq(coordinates[0])).and(qCoordinates.yCoordinates.eq(coordinates[1])));
//        User user = User.joinUser(coordinate.get(), password, userDTO.getName(), userDTO.getCapsuleType(),
//                userDTO.getOpenDayType(), writeableAt, null);
        //임시코드
        Coordinates dummyCoordinates = new Coordinates().newCoordinates(coordinates[0], coordinates[1]);
        coordinatesRepository.save(dummyCoordinates);
        userDTO.setName("테스트캡슐");
        userDTO.setCapsuleType(User.CapsuleType.EGG);
        //임시코드끝
        User user = User.joinUser(dummyCoordinates, password, userDTO.getName(), userDTO.getCapsuleType(),
                userDTO.getOpenDayType(), writeableAt, null);
        userRepository.save(user);
    }

    /**
     * 작성가능 날짜는 임시로 오픈일로부터 3개월 전 까지만 가능하도록 계산함
     * @param openDay
     * @return
     */
    private ZonedDateTime calculationWritingDays(User.OpenDay openDay) {
        LocalDate renderOpenDay = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), DateTimeFormatter.ISO_LOCAL_DATE);
        return LocalDate.parse(renderOpenDay.toString(), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(TimeZone.getDefault().toZoneId()).minusMonths(1);
    }

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
     * User 삭제기능 / 관리자에 의해 일부만 사용될 것으로 생각됨
     * @param userDTO
     */
    public void deleteUser(UserDto userDTO) {

//        User user = userRepository.findOne()

//        userRepository.delete(us);
    }

}
