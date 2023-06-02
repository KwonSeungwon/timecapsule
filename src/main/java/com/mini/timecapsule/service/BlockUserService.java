package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.BlockUserRepository;
import com.mini.timecapsule.dto.BlockUserDto;
import com.mini.timecapsule.model.BlockUser;
import com.mini.timecapsule.model.QBlockUser;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BlockUserService {

    private final BlockUserRepository blockUserRepository;

    public Iterable<BlockUser> list(BlockUserDto blockUserDto) {

        QBlockUser qBlockUser = QBlockUser.blockUser;
        BooleanBuilder predicate = new BooleanBuilder();

        if (blockUserDto.getId() != null) {
            predicate.and(qBlockUser.id.eq(blockUserDto.getId()));
        }
        if (blockUserDto.getCreatedAt() != null) {

        }
        return blockUserRepository.findAll(predicate);
    }


    public Boolean create(BlockUserDto blockUserDto) {


        BlockUser blockUser = new BlockUser();

        return null;
    }

    public Boolean update(BlockUserDto blockUserDto) {

        QBlockUser qBlockUser = QBlockUser.blockUser;
        BooleanBuilder predicate = new BooleanBuilder();

        Optional<BlockUser> blockUser = blockUserRepository.findOne(predicate.and(qBlockUser.id.eq(blockUserDto.getId())));

        blockUser.ifPresent(bu -> bu.update(blockUserDto.getMemo()));

        return blockUser.isPresent();
    }

    public boolean delete(BlockUserDto blockUserDto) {
        QBlockUser qBlockUser = QBlockUser.blockUser;
        BooleanBuilder predicate = new BooleanBuilder();

        Optional<BlockUser> blockUser = blockUserRepository.findOne(predicate.and(qBlockUser.id.eq(blockUserDto.getId())));

        blockUser.ifPresent(bu -> blockUserRepository.delete(bu));

        return blockUser.isPresent();
    }







}
