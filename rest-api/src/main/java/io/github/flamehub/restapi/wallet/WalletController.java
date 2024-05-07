package io.github.flamehub.restapi.wallet;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.wallet.api.WalletUser;
import io.github.flamehub.wallet.api.WalletUserMoneyChange;
import io.github.flamehub.wallet.api.WalletUserMoneyChangeType;
import io.github.flamehub.wallet.api.WalletUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@RestController
class WalletController {

    private final static String API_KEY = "VH5HGLWrABELd4lPoMTEpUxVn8CyQVJy";

    private final RedisMessenger redisMessenger;
    private final NetworkPlayerCache networkPlayerCache;
    private final WalletUserRepository walletUserRepository;

    @Autowired
    WalletController(NetworkPlayerCache networkPlayerCache, RedisMessenger redisMessenger, WalletUserRepository walletUserRepository) {
        this.networkPlayerCache = networkPlayerCache;
        this.redisMessenger = redisMessenger;
        this.walletUserRepository = walletUserRepository;
    }

    @PostMapping("/wallet/user/money/add/{user}/{amount}")
    void addMoney(
            final @PathVariable("user") String user,
            final @PathVariable("amount") double amount,
            final @RequestHeader("api-key") String apiKey
    ) {

        if (!API_KEY.equals(apiKey)) {
            throw new RuntimeException("Invalid API key");
        }

        final WalletUser walletUser = this.walletUserRepository.loadIgnoreCase("name", user);
        if (walletUser == null) {
            return;
        }

        final NetworkPlayer networkPlayer = networkPlayerCache.findByName(user);
        if (networkPlayer == null) {
            walletUser.addMoney(BigDecimal.valueOf(amount));
            CompletableFuture.supplyAsync(() -> walletUserRepository.save(walletUser));
            return;
        }

        this.redisMessenger.publish(networkPlayer.getServer(), new WalletUserMoneyChange(user, WalletUserMoneyChangeType.ADD, amount));
    }



}
