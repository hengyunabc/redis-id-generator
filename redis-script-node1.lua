
-- need redis 3.2+
redis.replicate_commands();

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

local now = redis.call('TIME');

local miliSecondKey = prefix .. tag ..'_' .. partition .. '_' .. now[1] .. '_' .. math.floor(now[2]/1000);

local count;
repeat
  count = tonumber(redis.call('INCRBY', miliSecondKey, step));
  if count > (1024 - step) then
      now = redis.call('TIME');
      miliSecondKey = prefix .. tag ..'_' .. partition .. '_' .. now[1] .. '_' .. math.floor(now[2]/1000);
  end
until count <= (1024 - step)

if count == step then
  redis.call('PEXPIRE', miliSecondKey, 5);
end

-- second, microSecond, partition, seq
return {tonumber(now[1]), tonumber(now[2]), partition, count + startStep}
