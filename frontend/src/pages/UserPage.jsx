import React from 'react';
import { useQuery } from '@tanstack/react-query';

const waitingId = '1';

const uri = `http://localhost:8080/api/v1/customer/${waitingId}`;

const fetchWaitingData = async () => {
  try {
    const response = await fetch(uri);
    if (!response.ok) {
      throw new Error('데이터를 가져오는 데 실패했습니다.');
    }
    const data = await response.json();
    return data;
  } catch (error) {
    throw new Error('데이터를 가져오는 중 오류 발생:', error);
  }
};

export default function UserPage() {
  const {
    data: waitingData,
    isLoading,
    isError,
    error,
  } = useQuery('waitingData', fetchWaitingData);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (isError) {
    return <div>Error: {error.message}</div>;
  }

  return (
    <div>
      <h1>UserPage - 웨이팅 확인</h1>
      <ul>
        {waitingData.map((item) => (
          <li key={item.id}>
            {item.name} - {item.waitingTime}분
          </li>
        ))}
      </ul>
    </div>
  );
}
