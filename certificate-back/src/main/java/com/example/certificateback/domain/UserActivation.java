package com.example.certificateback.domain;

import com.example.certificateback.configuration.ApplicationConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class UserActivation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.EAGER)
    private User user;
    @Column(name = "date", nullable = false)
    private Date date;
    @Column(name = "life", nullable = false)
    private int life;

    public UserActivation(User user){
        this.user = user;
        this.date = new Date();
        this.life = ApplicationConstants.USER_ACTIVATION_LIFE_IN_SECONDS;
    }
}
