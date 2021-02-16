def warn(*message, uplevel: nil)
  warn_without_color(Buildr::Console.color(message.join(' '), :blue), :uplevel => uplevel) if verbose
end
