package io.owlcult.dev.login;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class LoginCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
    		Commands.literal("login")
	    		.executes(context -> {
	                context.getSource().sendFailure(
	                    Component.literal("Используйте: /login <password>")
	                );
	                return 0;
	            })
	        	.then(Commands.argument("password", StringArgumentType.string())
		            .executes(
		                context -> {
		                	String password = StringArgumentType.getString(context, "password");
		                	if (password.isBlank())
		                		context.getSource().sendFailure(Component.literal("Введите пароль /login <password>"));
		                	else
		                		
		                		context.getSource().sendSuccess(() -> Component.literal("Успешный вход"), false);
		                    
		                    return 1;
		                }
		            )
	            )
        );
    }

}
