package br.com.jobinder.identityservice.domain.location;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity(name = "Location")
@Table(name = "locations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"city", "state"})
})
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String state;
}