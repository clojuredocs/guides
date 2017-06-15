Jekyll::Hooks.register :pages, :pre_render do |page, payload|

  # get the current page last modified time
  modification_time = File.mtime( page.path )

  # inject modification_time in page's datas.
  payload['page']['last-modified-date'] = modification_time

end
