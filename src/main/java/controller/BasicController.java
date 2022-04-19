package controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
@Slf4j
public class BasicController {
    @RequestMapping("/")
    public String home(){
        log.info("home Controller");
        return "home"; //home.jsp 파일로 찾아가서 타임리프파일을 찾아간다 .
    }
}
