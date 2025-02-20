-- 锁的值 与 当前线程id 一致时，才释放锁
if(redis.call('get', KEYS[1]) == ARGV[1]) then
    return redis.call('del', KEYS[1])
end
return 0