class Buildr::Project
  def force_no_uploads!(element)
    class << element
      def upload
      end
    end
  end

  def no_uploads!
    project.packages.each {|pkg|force_no_uploads!(pkg)}
    project.gpg = false
  end
end
