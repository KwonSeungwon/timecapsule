package service;

import model.TimeCapsule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.TimeCapsuleRepository;

@Service
public class TimeCapsuleService {

    @Autowired
    private TimeCapsuleRepository timeCapsuleRepository;

    public void create() {
        timeCapsuleRepository.save(new TimeCapsule(1, "2", "3", "4"));
    }
}
