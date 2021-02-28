package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {
    //projection 관련

//    @Value("#{target.username + ' ' + target.age}")
    String getUsername();

}
