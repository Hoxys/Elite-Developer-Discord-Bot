package com.xelitexirish.elitedeveloperbot;

import com.xelitexirish.elitedeveloperbot.commands.*;
import com.xelitexirish.elitedeveloperbot.listeners.BotListener;
import com.xelitexirish.elitedeveloperbot.listeners.SpellCheckerListener;
import com.xelitexirish.elitedeveloperbot.utils.*;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.Guild;

import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static JDA jda;
    public static final CommandParser parser = new CommandParser();
    public static HashMap<String, ICommand> commands = new HashMap<>();

    public static String DISCORD_TOKEN;
    public static boolean enableAutoMessages = true;
    public static boolean enableSpellChecker = true;

    // https://discordapp.com/oauth2/authorize?client_id=207593082328186880&scope=bot&permissions=0

    /**
     * 1: Discord Token
     * 2: Display messages when a user joins/bans/unbans
     * @param args
     */
    public static void main(String[] args) {

        if(args.length >= 3){
            DISCORD_TOKEN = args[0];
            enableAutoMessages = Boolean.parseBoolean(args[1]);
            enableAutoMessages = Boolean.parseBoolean(args[2]);
        }else{
            System.out.println("Please enter a valid Discord Token!");
            return;
        }

        BotLogger.initLogger();

        try {
            jda = new JDABuilder().setBotToken(DISCORD_TOKEN).setAudioEnabled(false).addListener(new BotListener()).buildBlocking();
            jda.setAutoReconnect(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        SpellCheckerListener.init();
        registerCommands();
        UserPrivs.setupUsers();
        enableCommandLineMessenger();
        //WarningHandler.setup();
    }

    private static void registerCommands(){
        commands.put("help", new HelpCommand());
        commands.put("admin", new AdminCommand());
        commands.put("projects", new ProjectsCommands());
        commands.put("userid", new PlayerIdCommand());
        commands.put("correction", new SpellCheckerCommand());
        //commands.put("warn", new WarningCommand());
    }

    public static void handleCommand(CommandParser.CommandContainer cmd){
        if(commands.containsKey(cmd.invoke)){
            boolean safe = commands.get(cmd.invoke).called(cmd.args, cmd.event);

            if(safe){
                commands.get(cmd.invoke).action(cmd.args, cmd.event);
                commands.get(cmd.invoke).executed(safe, cmd.event);
            }else {
                commands.get(cmd.invoke).executed(safe, cmd.event);
            }
        }
    }

    public static void enableCommandLineMessenger(){
        Scanner consoleScanner = new Scanner(System.in);
        String input = consoleScanner.nextLine();

        if(input.startsWith("console")) {
            String message = input.substring(7);

            for (int x = 0; x < jda.getGuilds().size(); x++) {
                Guild guild = jda.getGuilds().get(x);

                if(guild.getId().equals(Constants.SSL_DISCORD_ID)) {
                    guild.getPublicChannel().sendMessage(MessageUtils.wrapStringInCodeBlock("[CONSOLE] " + message));
                    BotLogger.log("console ", message);
                }
            }
        }else if(input.startsWith("allowUsers")){
            String message = input.substring(11);
            boolean enable = Boolean.parseBoolean(message);

            System.out.println(enable);
        }
    }
}
