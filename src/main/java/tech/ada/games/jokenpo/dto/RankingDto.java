package tech.ada.games.jokenpo.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RankingDto {
    public Long ranking;
    public String name;
    public String username;
    public Long victories;
}
