package com.smart_learn.core;

import com.smart_learn.models.Player;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Scope("prototype")
@Getter
@Setter
public class GameConnectionData {
    // how many player can participate to game
    private int maxPlayers;

    // player from first position will be player A and player from second position will be player B
    private final List<Player> players = new ArrayList<>();
    private final List<String> symbolList = new ArrayList<>();
    private String gameCode;
    private boolean localhostPlay;
    private String localIp;
    private boolean gameCanceled;
    private AtomicBoolean stopGame = new AtomicBoolean(false);
}
