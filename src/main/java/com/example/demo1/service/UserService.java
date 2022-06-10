package com.example.demo1.service;

import com.example.demo1.entity.User;
import com.example.demo1.repository.UserRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Log4j2
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void saveData(List<User> users) {
        userRepository.saveAll(users);
//        email="^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
//                phoneNumber =  "^(\\+\\d{2}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$";;

    }

    public String savedCsvFileInDb() {
        String status = "";
        try {
            CSVReader br = new CSVReaderBuilder(new FileReader("C:/Users/Vishnu/IdeaProjects/demo1/src/main/resources/user.csv"))
                    .withSkipLines(1).build();
            List<User> users = br.readAll().stream().map(strings -> {
                User user = new User();
                user.setId(Integer.parseInt(strings[0]));
                user.setName(strings[1]);
                user.setEmail(strings[2]);
                user.setPhoneNumber(strings[3]);
                return user;
            }).collect(Collectors.toList());

            users.forEach(user -> {
                Pattern emailPattern = Pattern.compile("^(.+)@(\\S+)$");
                Matcher emailMatcher = emailPattern.matcher(user.getEmail());
                if (emailMatcher.find()) {
                    try {
                        log.info("Saving the email address");
                        userRepository.save(user);
                    } catch (Exception e) {
                        throw new RuntimeException("Invalid emailID " + e);
                    }
                } else log.info("Invalid emailID");


                Pattern phonePattern = Pattern.compile("^\\d{10}$");
                Matcher phoneMatcher = emailPattern.matcher(user.getPhoneNumber());
                if (emailMatcher.find()) {
                    try {
                        log.info("Saving the Phone number");
                        userRepository.save(user);
                    } catch (Exception e) {
                        throw new RuntimeException("Invalid PhoneNumber " + e);
                    }
                } else log.info("Invalid PhoneNumber");

                userRepository.save(user);
            });
            status = "Records inserted successfully";
        } catch (IOException | CsvException e) {
            status = "Failed to insert";
            throw new RuntimeException(e);
        }

        return status;
    }
}



