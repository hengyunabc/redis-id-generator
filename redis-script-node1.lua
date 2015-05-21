local prefix = '__idgenerator_';
local partitionCount = 4096;
local step = 3;
local startStep = 0;

local tag = KEYS[1];
-- if user do not pass shardId, default partition is 0.
local partition
if KEYS[2] == nil then
  partition = 0;
else
  partition = KEYS[2] % partitionCount;
end

local miliSecondKey = prefix .. tag ..'_' .. partition;

local count;
repeat
  count = tonumber(redis.call('INCRBY', miliSecondKey, step));
until count < (1024 - step)

if count == step then
  redis.call('PEXPIRE', miliSecondKey, 1);
end

local now = redis.call('TIME');
-- second, microSecond, partition, seq
return {tonumber(now[1]), tonumber(now[2]), partition, count + startStep}
