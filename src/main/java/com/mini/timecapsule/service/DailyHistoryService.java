package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.DailyHistoryRepository;
import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.model.DailyHistory;
import com.mini.timecapsule.model.LetterPaper;
import com.mini.timecapsule.model.QDailyHistory;
import com.mini.timecapsule.model.QLetterPaper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

@Service
@Transactional
@RequiredArgsConstructor
public class DailyHistoryService {

    private final DailyHistoryRepository dailyHistoryRepository;

    private final LetterPaperRepository letterPaperRepository;

    public List<DailyHistory> findAll() {

        QDailyHistory qDailyHistory = QDailyHistory.dailyHistory;
        BooleanBuilder predicate = new BooleanBuilder();

        return dailyHistoryRepository.findAll();
    }


    public void create() {

        QLetterPaper qLetterPaper = QLetterPaper.letterPaper;
        BooleanBuilder predicate = new BooleanBuilder();
        ZonedDateTime startDate = LocalDate.parse(LocalDate.now().toString(), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(TimeZone.getDefault().toZoneId());
        ZonedDateTime endDate = LocalDate.parse(LocalDate.now().toString(), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(TimeZone.getDefault().toZoneId()).plusDays(1);
        LocalDate today = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), DateTimeFormatter.ISO_LOCAL_DATE);
        predicate.and(qLetterPaper.createdAt.between(startDate, endDate));

        List<LetterPaper> letterPapers = (List<LetterPaper>) letterPaperRepository.findAll(predicate);




        DailyHistory dailyHistory = DailyHistory.newDailyHistory(today.toString(), (long) letterPapers.size(), "", (long) 0, letterPapers.get(0).getCreatedAt(),
                letterPapers.get(letterPapers.size() - 1).getCreatedAt(), (long) letterPapers.size());

    }

    public String mostRequestEmail(List<LetterPaper> letterPapers) {
        HashMap<String, Integer> emailCountingMap = new HashMap<>();

        for (LetterPaper letterPaper : letterPapers) {
        }

        return null;
    }

//    private BooleanExpression eqDate(LocalDate today) {
//        return today != null ?  QLetterPaper.letterPaper.createdAt.eq(today):
//
//    }


}
