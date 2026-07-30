// 이 서비스의 "하루" 경계는 사용자가 어디에 있든 언제나 KST 다 (백엔드 LetterPaper.SERVICE_ZONE).
// 서버는 openable 을 openAt 인스턴트 비교로, D-Day 를 KST 달력 날짜 차이로 계산한다.
// 그래서 날짜를 브라우저 로컬 시간대로 계산하거나 표시하면 KST 밖 사용자에게 둘이 어긋난다 —
// openAt = 2026-08-01T00:00+09:00 인 캡슐이 미국 서부 브라우저에서 "2026.07.31 개봉" 으로
// 렌더되고, 바로 옆에는 8/1 을 세는 서버 D-Day 가 붙어 한 화면에 날짜가 두 개 뜬다.
//
// 사용자에게 보이는 모든 날짜와 모든 날짜 산술은 이 모듈을 거친다.
// 서버로 보낼 때만 KST 자정 인스턴트로 바꾼다.
// KST 는 서머타임이 없어 고정 오프셋으로 충분하다.
export const SERVICE_TIME_ZONE = 'Asia/Seoul';

const SERVICE_UTC_OFFSET = '+09:00';
const MS_PER_DAY = 24 * 60 * 60 * 1000;

// en-CA 로케일이 YYYY-MM-DD 를 준다. 로케일 표기에 흔들리지 않는 파싱 가능한 키가 필요해서다.
const SERVICE_ZONE_ISO_DATE = new Intl.DateTimeFormat('en-CA', { timeZone: SERVICE_TIME_ZONE });

/** 'YYYY-MM-DD' → 그 날 KST 자정의 ISO 인스턴트. 서버로 나가는 유일한 형태다. */
export function toServiceZoneInstant(dateString) {
  return new Date(`${dateString}T00:00:00${SERVICE_UTC_OFFSET}`).toISOString();
}

/** 오늘 날짜를 KST 달력 기준 'YYYY-MM-DD' 로. */
export function serviceToday() {
  return SERVICE_ZONE_ISO_DATE.format(new Date());
}

/**
 * 'YYYY-MM-DD' 에서 n 일 뒤.
 * 두 값 모두 UTC 자정으로 파싱해 더하므로 로컬 시간대나 서머타임의 영향을 받지 않는다.
 */
export function addDays(dateString, days) {
  return new Date(Date.parse(`${dateString}T00:00:00Z`) + days * MS_PER_DAY)
      .toISOString()
      .slice(0, 10);
}

/** 달력 날짜 사이의 일수. 서버의 D-Day(ChronoUnit.DAYS.between) 와 같은 정의다. */
export function daysBetween(fromDateString, toDateString) {
  return Math.round(
      (Date.parse(`${toDateString}T00:00:00Z`) - Date.parse(`${fromDateString}T00:00:00Z`)) / MS_PER_DAY);
}

/**
 * 서버가 준 인스턴트(ZonedDateTime 직렬화값)를 KST 달력 날짜 'YYYY-MM-DD' 로.
 * 파싱할 수 없으면 null — 대체 문구는 화면마다 달라서 호출부가 고른다.
 */
export function toServiceDate(instant) {
  if (!instant) return null;
  const date = new Date(instant);
  if (Number.isNaN(date.getTime())) return null;
  return SERVICE_ZONE_ISO_DATE.format(date);
}

/**
 * KST 로 고정한 로케일 표기.
 * 구분자·자릿수는 브라우저 로케일을 따르되 가리키는 날짜는 항상 KST 다.
 */
export function formatServiceDateLocalized(instant) {
  if (!instant) return null;
  const date = new Date(instant);
  if (Number.isNaN(date.getTime())) return null;
  return date.toLocaleDateString(undefined, { timeZone: SERVICE_TIME_ZONE });
}
