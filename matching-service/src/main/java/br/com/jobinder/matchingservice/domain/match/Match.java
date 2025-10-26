package br.com.jobinder.matchingservice.domain.match;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "matches", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"clientUserId", "professionalUserId"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID clientUserId;

    @Column(nullable = false)
    private UUID professionalUserId;

    @CreationTimestamp
    private LocalDateTime createdAt;
}