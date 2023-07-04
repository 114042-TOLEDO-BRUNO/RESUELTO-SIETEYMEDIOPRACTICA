package ar.edu.utn.frc.tup.lciii.services.impl;

import ar.edu.utn.frc.tup.lciii.dtos.player.NewPlayerRequestDTO;
import ar.edu.utn.frc.tup.lciii.dtos.player.PlayerResponseDTO;
import ar.edu.utn.frc.tup.lciii.entities.PlayerEntity;
import ar.edu.utn.frc.tup.lciii.models.Player;
import ar.edu.utn.frc.tup.lciii.repositories.jpa.PlayerJpaRepository;
import ar.edu.utn.frc.tup.lciii.services.PlayerService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService {

    private static final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(1000);
    private static final String DEFAULT_AVATAR = "/avatars/default/default_1.png";

    @Autowired
    private PlayerJpaRepository playerJpaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Player getPlayerById(Long id) {
        PlayerEntity playerEntity = playerJpaRepository.getReferenceById(id);
        if(Objects.isNull(playerEntity.getUserName())) {
            throw new EntityNotFoundException(String.format("The player id %s do not exist", id));
        }
        return modelMapper.map(playerEntity, Player.class);
    }

    @Override
    public PlayerResponseDTO getPlayerResponseDTOById(Long id) {
        PlayerEntity playerEntity = playerJpaRepository.getReferenceById(id);
        if(Objects.isNull(playerEntity.getUserName())) {
            throw new EntityNotFoundException(String.format("The player id %s do not exist", id));
        }
        return modelMapper.map(playerEntity, PlayerResponseDTO.class);
    }

    @Override
    public Player updatePlayerBalance(Long playerId, BigDecimal balanceChipsImpact) {
        // TO DO: Implementar el método de manera tal que impacte en el usuario el balancImpact pasado por parametro.
        //  Como resultado del guardado debe retornar el usuario nuevamente con el balance actualizado.
        //  Es decir que...->  nuevoBalance = actualBalance + balanceChipsImpact
        Player player=getPlayerById(playerId);
        player.setBalanceChips(player.getBalanceChips().add(balanceChipsImpact));
        playerJpaRepository.saveAndFlush(modelMapper.map(player,PlayerEntity.class));
        return player;

    }

    @Override
    public PlayerResponseDTO createNewPlayer(NewPlayerRequestDTO newPlayerRequestDTO) throws IllegalAccessException {
        // TO DO: Implementar el método de manera tal que cree un nuevo usuario con los datos recibidos por parametro
        //  y asigne por unica vez 1000 fichas de regalo en el balance. El metodo debe validar que no exista
        //  otro usuario con el mismo user_name o email; si existe, debe retornar una exepcion del
        //  tipo IllegalArgumentException con el mensaje "The user_name or email already exists"
        //  Ayuda: Usar el metodo userAvailable()
        if(userAvailable(newPlayerRequestDTO.getEmail(), newPlayerRequestDTO.getUserName())) {
            PlayerEntity playerEntity = new PlayerEntity();
            // TO DO: Completar aquí
            playerEntity.setUserName(newPlayerRequestDTO.getUserName());
            playerEntity.setPassword(newPlayerRequestDTO.getPassword());
            playerEntity.setEmail(newPlayerRequestDTO.getEmail());
            playerEntity.setBalanceChips(INITIAL_BALANCE);
            playerEntity.setAvatar(DEFAULT_AVATAR);
            playerEntity.setId(playerJpaRepository.saveAndFlush(playerEntity).getId());
            return modelMapper.map(playerEntity, PlayerResponseDTO.class);
        } else {
            throw new IllegalArgumentException("The user_name or email already exists");
        }
    }

    private Boolean userAvailable(String email, String userName) {
        // TO DO: Implementar el método de manera tal que valide contra la base de datos que no exista un jugador
        //  con el email o el userName recibidos por paarmetros
       Optional<PlayerEntity> playerEntity=playerJpaRepository.findByUserNameOrEmail(userName,email);
        return playerEntity.isEmpty();
    }
}
