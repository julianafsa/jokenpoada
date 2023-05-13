package tech.ada.games.jokenpo.dto;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RankingDto {
    public Long ranking;
    public String name;
    public String username;
    public Long victories;

}
