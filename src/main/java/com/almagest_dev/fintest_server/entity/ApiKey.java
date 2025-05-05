package com.almagest_dev.fintest_server.entity;

import com.almagest_dev.fintest_server.exception.base_exceptions.ValidationException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Setter
@Table(name = "api_key")
public class ApiKey extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    @Column(nullable = false, unique = true, length = 43, columnDefinition = "VARCHAR(43)")
    @Comment("API KEY")
    private String apiKey;

    @Column(name = "api_call_count", nullable = false, columnDefinition = "INT")
    @Comment("일일 API 호출 횟수")
    @Min(value = 0)
    @Max(value = 1000)
    private Integer apiCallCount;


    @Column(nullable = false, length = 1, columnDefinition = "VARCHAR(1)")
    @Comment("키 가용 여부")
    private String isAvailable;


    public void decreaseCallCount(){
        if(this.apiCallCount > 0) {
            this.apiCallCount--;
        }else{
            throw new ValidationException("API 일일 호출횟수 초과");
        }
    }
}
