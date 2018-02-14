package me.Patricktopgames;

import com.coloredcarrot.jsonapi.impl.JsonClickEvent;
import com.coloredcarrot.jsonapi.impl.JsonColor;
import com.coloredcarrot.jsonapi.impl.JsonHoverEvent;
import com.coloredcarrot.jsonapi.impl.JsonMsg;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
   private ArrayList<Player> tpdesativado = new ArrayList<Player>();
   private HashMap<String, Long> cdtpa = new HashMap<String, Long>();
   private HashMap<String, Long> cd = new HashMap<String, Long>();

   public void addCd(String nome) {
      this.cd.put(nome, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
   }

   public boolean hasCd(String nome) {
      return this.cd.containsKey(nome);
   }

   public long restanteCd(String nome) {
      Long delay = 30L;
      long segundosRestante = (Long)this.cd.get(nome) + delay - TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
      return segundosRestante;
   }

   public void onEnable() {
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      Player p = (Player)sender;
      if (p instanceof Player) {
         Player v;
         if (cmd.getName().equalsIgnoreCase("tpa")) {
            if (args.length == 1) {
               v = Bukkit.getPlayerExact(args[0]);
               if (p == v) {
                  p.sendMessage("§6Você não pode teleportar-se para si mesmo");
                  return false;
               }

               if (v == null) {
                  p.sendMessage("§6Jogador não encontrado, tente novamente.");
                  return false;
               }

               if (this.tpdesativado.contains(v)) {
                  p.sendMessage("§6Esse jogador está com pedidos de teleporte desligado");
                  return false;
               }

               String cdveri = p.getName() + ":" + v.getName();
               if (this.hasCd(cdveri)) {
                  this.cd.remove(cdveri);
               }

               this.addCd(cdveri);
               p.sendMessage("§6Pedido de teleporte enviado");
               JsonMsg msg = new JsonMsg("Pedido de teleporte de ", JsonColor.GOLD);
               JsonMsg nome = new JsonMsg(p.getName() + " ", JsonColor.RED);
               JsonMsg confirmar = new JsonMsg("[ACEITAR]", JsonColor.GREEN);
               JsonMsg recusar = new JsonMsg("[RECUSAR]", JsonColor.DARK_RED);
               confirmar.style(ChatColor.BOLD);
               recusar.style(ChatColor.BOLD);
               confirmar.hoverEvent(JsonHoverEvent.showText("Clique aqui para aceitar", JsonColor.GRAY));
               recusar.hoverEvent(JsonHoverEvent.showText("Clique aqui para recusar", JsonColor.GRAY));
               confirmar.clickEvent(JsonClickEvent.runCommand("/tpaceitar " + p.getName()));
               recusar.clickEvent(JsonClickEvent.runCommand("/tprecusar " + p.getName()));
               msg.append(nome);
               msg.append(confirmar);
               msg.append(recusar);
               msg.send(new Player[]{v});
               return true;
            }

            p.sendMessage("§c{!) Para enviar um pedido de teleporte use /tpa <nome>");
         }

         if (cmd.getName().equalsIgnoreCase("tpaceitar")) {
            if (args.length == 1) {
               v = Bukkit.getPlayerExact(args[0]);
               if (p == v) {
                  return false;
               }

               if (v == null) {
                  p.sendMessage("§6Jogador não encontrado, tente novamente.");
                  return false;
               }

               this.aceitar(v, p);
               return true;
            }

            p.sendMessage("§c{!) Para aceitar um pedido de teleporte use /tpaceitar <nome>");
         }

         if (cmd.getName().equalsIgnoreCase("tprecusar")) {
            if (args.length == 1) {
               v = Bukkit.getPlayerExact(args[0]);
               if (p == v) {
                  return false;
               }

               if (v == null) {
                  p.sendMessage("§6Jogador não encontrado, tente novamente.");
                  return false;
               }

               this.recusar(v, p);
               return true;
            }

            p.sendMessage("§c{!) Para aceitar um pedido de teleporte use /tprecusar <nome>");
         }

         if (cmd.getName().equalsIgnoreCase("tpdesativar")) {
            if (p.hasPermission("gctpa.desativar")) {
               if (this.tpdesativado.contains(p)) {
                  this.tpdesativado.remove(p);
                  p.sendMessage("§6Pedidos de teleportes ativado");
                  return true;
               }

               this.tpdesativado.add(p);
               p.sendMessage("§6Pedidos de teleportes desativado");
               return true;
            }

            p.sendMessage("§4Você não possui permissão para executar esse comando.");
         }
      }

      return false;
   }

   public void aceitar(Player p, Player v) {
      String cdveri = p.getName() + ":" + v.getName();
      if (this.hasCd(cdveri)) {
         if (this.restanteCd(cdveri) > 0L) {
            Location pos = v.getLocation();
            p.teleport(pos);
            this.cd.remove(cdveri);
         } else {
            v.sendMessage("§6Pedido de teleporte expirado");
         }
      } else {
         v.sendMessage("§6Pedido de teleporte inexistente");
      }

   }

   public void recusar(Player p, Player v) {
      String cdveri = p.getName() + ":" + v.getName();
      if (this.hasCd(cdveri)) {
         if (this.restanteCd(cdveri) > 0L) {
            this.cd.remove(cdveri);
            p.sendMessage("§6Seu pedido de teleporte foi recusado");
            v.sendMessage("§6Pedido de teleporte recusado.");
         } else {
            v.sendMessage("§6Pedido de teleporte expirado");
         }
      } else {
         v.sendMessage("§6Pedido de teleporte inexistente");
      }

   }

public HashMap<String, Long> getCdtpa() {
	return cdtpa;
}

public void setCdtpa(HashMap<String, Long> cdtpa) {
	this.cdtpa = cdtpa;
}
}