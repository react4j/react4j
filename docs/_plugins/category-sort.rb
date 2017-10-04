module Jekyll
  module CategorySortFilter
    CATEGORY_MAP =
      {
        'Overview' => 1
      }

    def category_sort(input)
      input.sort_by {|entry| CATEGORY_MAP[entry['name']] || 100}
    end
  end
end

Liquid::Template.register_filter(Jekyll::CategorySortFilter)
