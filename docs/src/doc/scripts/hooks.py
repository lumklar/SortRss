import os

def on_config(config):
    # 读取环境变量，若未设置则保留原 yml 中的值（作为 fallback）
    repo_url = os.environ.get('REPO_URL')
    if repo_url:
        config.repo_url = repo_url

    site_url = os.environ.get('SITE_URL')
    if site_url:
        config.site_url = site_url

    version = os.environ.get('VERSION')
    if version:
        # 确保 extra 中有 version 键，且其值是一个字典
        if 'version' not in config.extra:
            config.extra['version'] = {}
        config.extra['version']['default'] = version

    return config