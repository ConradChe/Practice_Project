/**
 * 系统配置文件
 */
var CACHE_TYPE = "page";// 缓存方式page,local
var BASE_PATH = "/school"; // 框架地址
var DICT_REQUEST_URL = "http://localhost/school/common/get_dicts.do"; // 查询数据字典接口
var FILE_UPLOAD_URL = "http://localhost/school/common/image_base64_upload.do"; // 文件上传接口
var FILE_DELETE_URL = "http://localhost/school/common/media_file_delete.do"; // 文件删除接口

/**
 * 主服务器
 */
var DEFAULT_SERVER = "http://localhost/school/common/data.do"; // 主服务器

// 导入框架文件
document.write('<script src="' + BASE_PATH + '/frame/frame.js"></script>');