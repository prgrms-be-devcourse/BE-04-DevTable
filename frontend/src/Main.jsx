import { Link } from 'react-router-dom';

export default function Main() {
  return (
    <main>
      <div className='flex flex-col items-center justify-center min-h-screen bg-gray-400'>
        <div className='flex space-x-4 justify-center'>
          <Link to='/userPage'>
            <button className='hover:bg-orange-600 py-12 px-16 border rounded-lg bg-orange-400 hover:bg-primary-800 focus:ring-4 focus:ring-primary-300 font-medium text-xl'>
              유저 기능
            </button>
          </Link>
          <Link to='/ownerPage'>
            <button className='hover:bg-orange-600 py-12 px-16 border rounded-lg bg-orange-400 hover:bg-primary-800 focus:ring-4 focus:ring-primary-300 font-medium text-xl'>
              점주 기능
            </button>
          </Link>
        </div>
        {/* 나머지 Main 컨텐츠 */}
      </div>
    </main>
  );
}
