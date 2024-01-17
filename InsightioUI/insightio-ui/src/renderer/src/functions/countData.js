const validTimeFrames = [
  { text: 'Day', value: 'day' },
  { text: 'Week', value: 'week' },
  { text: 'Month', value: 'month' },
  { text: 'Year', value: 'year' }
]

const labels = {
  day: Array.from({ length: 24 }, (_, i) => `${i}:00`),
  week: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'],
  month: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
  year: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
}

// Time frame to index mapping for counts array
const timeFrameToIndex = {
  day: 0,
  week: 1,
  month: 2,
  year: 3
}

const initTimeVisibilityArray = (currentTimeFrame) => {
  return Object.fromEntries(labels[currentTimeFrame].map((t) => [t, true]))
}

const getFramedCountData = (countData, currentTimeFrame) => {
  return countData.map((o) => {
    return {
      target: o.target,
      labels: labels[currentTimeFrame],
      data: o.counts[timeFrameToIndex[currentTimeFrame]],
      hidden: false
    }
  })
}

const CountHelper = {
  labels: labels,
  timeFrameToIndex: timeFrameToIndex,
  validTimeFrames: validTimeFrames,
  getFramedCountData: getFramedCountData,
  initTimeVisibilityArray: initTimeVisibilityArray
}

export default CountHelper;