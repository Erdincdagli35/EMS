package com.example.EMS;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class EmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmsApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(StudentRepository studentRepository) {
        return args -> {
            Address address = new Address(
                    "Turkey",
                    "Izmir",
                    "35"
            );

            String email = "erdincda@mail.com";

            Student student = new Student(
                    "Erdinç",
                    "Dağlı",
                    email,
                    Gender.MALE,
                    address,
                    List.of("Football"),
                    BigDecimal.TEN,
                    LocalDateTime.now()
            );

            studentRepository.findStudentByEmail(email)
                    .ifPresentOrElse(s -> {
                        System.out.println(s + "already exists");
                    }, () -> {
                        System.out.println("Inserting student : " + student);
                        studentRepository.insert(student);
                    });
        };
    }

    private void usingMongoTemplateAndQuery(StudentRepository studentRepository, MongoTemplate mongoTemplate, String email, Student student) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));

        List<Student> students = mongoTemplate.find(query, Student.class);
        if (students.size() > 1)
            throw new IllegalStateException("found many students with email " + email);

        if (students.isEmpty()) {
            System.out.println("Created : " + student);
            studentRepository.insert(student);
        } else {
            System.out.println("User create is failed");
        }
    }
}
