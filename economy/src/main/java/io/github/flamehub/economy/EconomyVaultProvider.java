package io.github.flamehub.economy;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.economy.user.updater.EconomyUserUpdate;
import io.github.flamehub.economy.user.updater.EconomyUserUpdater;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.economy.user.EconomyUser;
import io.github.flamehub.economy.user.EconomyUserCache;
import io.github.flamehub.economy.user.EconomyUserRepository;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

public class EconomyVaultProvider implements Economy {

    private final FlameDispatcher flameDispatcher;

    private final RedisMessenger redisMessenger;
    private final EconomyUserCache economyUserCache;
    private final EconomyUserRepository economyUserRepository;
    private final EconomyUserUpdater economyUserUpdater;
    private final NetworkPlayerCache networkPlayerCache;
    private final NetworkServerCache networkServerCache;

    public EconomyVaultProvider(FlameDispatcher flameDispatcher, RedisMessenger redisMessenger, EconomyUserCache economyUserCache, EconomyUserRepository economyUserRepository, EconomyUserUpdater economyUserUpdater, NetworkPlayerCache networkPlayerCache, NetworkServerCache networkServerCache) {
        this.flameDispatcher = flameDispatcher;
        this.redisMessenger = redisMessenger;
        this.economyUserCache = economyUserCache;
        this.economyUserRepository = economyUserRepository;
        this.economyUserUpdater = economyUserUpdater;
        this.networkPlayerCache = networkPlayerCache;
        this.networkServerCache = networkServerCache;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double v) {
        return new BigDecimal(v).setScale(2, RoundingMode.CEILING).toString();
    }

    @Override
    public String currencyNamePlural() {
        return currencyNameSingular();
    }

    @Override
    public String currencyNameSingular() {
        return "$";
    }

    @Override
    public boolean hasAccount(String s) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }

    @Override
    public double getBalance(String s) {

        EconomyUser economyUser = this.economyUserCache.findByName(s);
        return economyUser.getMoney().doubleValue();
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {

        EconomyUser economyUser = this.economyUserCache.findByUniqueId(offlinePlayer.getUniqueId());
        return economyUser.getMoney().doubleValue();

    }

    @Override
    public double getBalance(String s, String s1) {
        return getBalance(s);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return getBalance(offlinePlayer);
    }

    @Override
    public boolean has(String s, double v) {

        EconomyUser economyUser = this.economyUserCache.findByName(s);
        return economyUser.hasEnough(BigDecimal.valueOf(v));

    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {

        EconomyUser economyUser = this.economyUserCache.findByUniqueId(offlinePlayer.getUniqueId());
        return economyUser.hasEnough(BigDecimal.valueOf(v));

    }

    @Override
    public boolean has(String s, String s1, double v) {
        return has(s, v);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return has(offlinePlayer, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if (playerName == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Nazwa gracza nie może być nullem.");
        }

        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Nie można wypłacić ujemnej kwoty.");
        }

        EconomyUser economyUser = this.economyUserCache.findByName(playerName);
        if (economyUser == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Ten gracz nie istnieje w bazie danych!");
        }

        BigDecimal result = economyUser.getMoney().subtract(BigDecimal.valueOf(amount), MathContext.DECIMAL128);
        economyUser.setMoney(result);
        economyUser.setNeedUpdate(true);

        return new EconomyResponse(amount, economyUser.getMoney().doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        if (offlinePlayer == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "OfflinePlayer cannot be null!");
        }
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Nie można wypłacić ujemnej kwoty.");
        }

        EconomyUser economyUser = this.economyUserCache.findByUniqueId(offlinePlayer.getUniqueId());
        if (economyUser == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Ten gracz nie istnieje w bazie danych!");
        }

        economyUser.removeMoney(amount);
        economyUser.setNeedUpdate(true);

        return new EconomyResponse(amount, economyUser.getMoney().doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return withdrawPlayer(s, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return withdrawPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        if (playerName == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Nazwa gracza nie może być nullem.");
        }
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Nie można wpłacić ujemnej kwoty.");
        }

        EconomyUser economyUser = this.economyUserCache.findByName(playerName);
        if (economyUser == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Ten gracz nie istnieje w bazie danych!");
        }

        economyUser.addMoney(amount);
        NetworkPlayer networkPlayer = this.networkPlayerCache.findByName(playerName);
        if (networkPlayer == null || !this.networkServerCache.getCurrent().getCategory().equals(this.networkServerCache.findByName(networkPlayer.getServer()).get().getCategory())) {
            this.flameDispatcher.dispatchAsync(() -> this.economyUserRepository.save(economyUser));
        }
        else if(!networkPlayer.getServer().equals(this.networkServerCache.getCurrent().getName()) && this.networkServerCache.getCurrent().getCategory().equals(this.networkServerCache.findByName(networkPlayer.getServer()).get().getCategory())) {
            this.redisMessenger.publish(networkPlayer.getServer(), new EconomyUserUpdate(networkPlayer.getUniqueId(), economyUser.getMoney().doubleValue()));
        }
        else {
            economyUser.setNeedUpdate(true);
        }

        return new EconomyResponse(amount, economyUser.getMoney().doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        if (offlinePlayer == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "OfflinePlayer cannot be null.");
        }
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Nie można wpłacić ujemnej kwoty.");
        }

        EconomyUser economyUser = this.economyUserCache.findByUniqueId(offlinePlayer.getUniqueId());
        if (economyUser == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Ten gracz nie istnieje w bazie danych!");
        }

        economyUser.addMoney(amount);
        NetworkPlayer networkPlayer = this.networkPlayerCache.findByUniqueId(offlinePlayer.getUniqueId());
        if (networkPlayer == null || !this.networkServerCache.getCurrent().getCategory().equals(this.networkServerCache.findByName(networkPlayer.getServer()).get().getCategory())) {
            this.flameDispatcher.dispatchAsync(() -> this.economyUserRepository.save(economyUser));
        }
        else if(!networkPlayer.getServer().equals(this.networkServerCache.getCurrent().getName()) && this.networkServerCache.getCurrent().getCategory().equals(this.networkServerCache.findByName(networkPlayer.getServer()).get().getCategory())) {
            this.redisMessenger.publish(networkPlayer.getServer(), new EconomyUserUpdate(networkPlayer.getUniqueId(), economyUser.getMoney().doubleValue()));
        }
        else {
            economyUser.setNeedUpdate(true);
        }

        return new EconomyResponse(amount, economyUser.getMoney().doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return depositPlayer(s, v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return depositPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }
}
