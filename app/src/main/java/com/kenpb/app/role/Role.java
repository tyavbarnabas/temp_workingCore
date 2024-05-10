package com.kenpb.app.role;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kenpb.app.menu.Menu;
import com.kenpb.app.permission.Permission;
import com.kenpb.app.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "base-role")
public class Role {
    @SequenceGenerator(name = "role_id", sequenceName = "role_id", allocationSize = 1)
    @GeneratedValue(generator = "role_id", strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;
    private String name;
    private String code;
    @LastModifiedDate
    @Column(insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateModified;
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreated;
    private String archived;
    @ManyToMany(mappedBy ="roles",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonIgnore
    private List<User> users;

}
