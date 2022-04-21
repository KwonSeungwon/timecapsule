package model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "time_capsule")
@DynamicUpdate
public class TimeCapsule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

}
