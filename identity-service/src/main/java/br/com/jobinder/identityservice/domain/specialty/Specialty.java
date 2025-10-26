package br.com.jobinder.identityservice.domain.specialty; // Pacote ajustado

import jakarta.persistence.*;
import lombok.*; // Imports ajustados
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity(name = "Specialty")
@Table(name = "specialties")
public class Specialty {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String name;
}