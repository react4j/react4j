def warn(*message)
  warn_without_color Buildr::Console.color(message.join(' '), :blue) if verbose
end
